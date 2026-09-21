package controller;

import config.constant.DiceConstants;
import config.enums.CommandType;
import config.enums.GameMessageType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import message.GameMessage;
import message.observer.GameMessagePublisher;
import model.board.Board;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.rule.block.BlockadeBreakRule;
import model.player.rule.capture.CaptureCheckRule;
import model.player.rule.mystery.BetaRestrictionRule;
import model.player.rule.mystery.MysteryCellTeleportRule;
import model.player.rule.roll.RollValidityRule;
import model.player.rule.turn.TurnRule;
import model.player.strategy.PlayerStrategy;
import model.player.strategy.PlayerStrategyRegistry;
import model.player.strategy.StrategyContext;
import utils.dice.Dice;

public final class GameEngine {

    private final List<TurnRule> turnRules;
    private final PlayerStrategyRegistry strategyRegistry;
    private final RollValidityRule rollValidityRule;
    private final CaptureCheckRule captureCheckRule;
    private final BlockadeBreakRule blockadeBreakRule;
    private final MysteryCellTeleportRule mysteryCellTeleportRule;
    private final BetaRestrictionRule betaRestrictionRule;
    private final HomeGateTracker homeGateTracker;

    public GameEngine(
            List<TurnRule> turnRules, PlayerStrategyRegistry strategyRegistry,
            RollValidityRule rollValidityRule, CaptureCheckRule captureCheckRule,
            BlockadeBreakRule blockadeBreakRule, MysteryCellTeleportRule mysteryCellTeleportRule,
            BetaRestrictionRule betaRestrictionRule, HomeGateTracker homeGateTracker) {
        this.turnRules = turnRules;
        this.strategyRegistry = strategyRegistry;
        this.rollValidityRule = rollValidityRule;
        this.captureCheckRule = captureCheckRule;
        this.blockadeBreakRule = blockadeBreakRule;
        this.mysteryCellTeleportRule = mysteryCellTeleportRule;
        this.betaRestrictionRule = betaRestrictionRule;
        this.homeGateTracker = homeGateTracker;
    }

    public void playTurn(
            Player player, List<Player> allPlayers, Dice dice, Board board,
            GameMessagePublisher messagePublisher) {
        messagePublisher.publish(GameMessage.turnStarted(player.getColor()));

        int rollNumber = 0;

        // Tracks consecutive sixes; resets on any non-six roll.
        int consecutiveSixCount = 0;
        boolean turnContinues = true;

        while (turnContinues) {
            rollNumber++;
            int rollValue = dice.roll();
            consecutiveSixCount = rollValue == DiceConstants.SIX_ROLL_VALUE ? consecutiveSixCount + 1 : 0;

            messagePublisher.publish(GameMessage.turnRolled(player.getColor(), rollValue));

            Optional<Command> forcedBreak =
                    blockadeBreakRule.findForcedBreak(player, consecutiveSixCount, rollValue, board, allPlayers);
            if (forcedBreak.isPresent()) {
                forcedBreak.get().execute(messagePublisher);
                applyCapture(player, forcedBreak.get(), allPlayers, messagePublisher);
                return;
            }

            if (rollValidityRule.isVoided(consecutiveSixCount, rollValue)) {
                messagePublisher.publish(GameMessage.of(GameMessageType.THIRD_SIX_VOIDED));
                return;
            }

            // Runs before legal commands, so strategy previews and the real move agree.
            updateHomeGate(player, allPlayers, messagePublisher);

            // T-13: forces still-restricted Beta piece back to Base.
            betaRestrictionRule.recordRoll(player, rollNumber, rollValue);
            betaRestrictionRule.findReturnToBase(player)
                    .ifPresent(command -> command.execute(messagePublisher));

            boolean capturedOpponent =
                    resolveAndPlay(player, allPlayers, rollNumber, rollValue, board, messagePublisher);

            turnContinues = grantsAnotherRoll(rollValue, capturedOpponent);
        }
    }

    // T-7 home gate: counts this roll and announces it once, on the roll that opens the gate.
    private void updateHomeGate(
            Player player, List<Player> allPlayers, GameMessagePublisher messagePublisher) {
        homeGateTracker.recordRoll(player, allPlayers);

        if (homeGateTracker.wasOpenedByLatestRoll(player.getColor())) {
            messagePublisher.publish(GameMessage.homeGateOpened(player.getColor()));
        }
    }

    private boolean resolveAndPlay(
            Player player, List<Player> allPlayers, int rollNumber, int rollValue, Board board,
            GameMessagePublisher messagePublisher) {
        List<Command> legalCommands = collectLegalCommands(player, allPlayers, rollValue, board);

        if (legalCommands.isEmpty()) {
            messagePublisher.publish(GameMessage.noPieceMovable());
            return false;
        }

        PlayerStrategy strategy = strategyRegistry.getStrategyFor(player.getColor());
        StrategyContext context = new StrategyContext(
                player, allPlayers, board, mysteryCellTeleportRule.getMysteryCellLocation(), rollNumber);

        Command chosenCommand = strategy.choose(legalCommands, context);
        chosenCommand.execute(messagePublisher);

        boolean capturedOpponent = applyCapture(player, chosenCommand, allPlayers, messagePublisher);
        boolean capturedByTeleport =
                applyMysteryCellTeleport(player, chosenCommand, allPlayers, messagePublisher);

        return capturedOpponent || capturedByTeleport;
    }

    // T-11: checks distinct landing positions for Mystery Cell; a teleport can capture too.
    private boolean applyMysteryCellTeleport(
            Player mover, Command executedCommand, List<Player> allPlayers,
            GameMessagePublisher messagePublisher) {
        Set<Integer> checkedPositions = new HashSet<>();
        boolean capturedAny = false;

        for (Piece movedPiece : executedCommand.getAffectedPieces()) {
            if (!movedPiece.isOnTrack() || !checkedPositions.add(movedPiece.getTrackPosition())) {
                continue;
            }

            Optional<Command> teleportCommand = mysteryCellTeleportRule.findTeleport(mover, movedPiece);

            if (teleportCommand.isPresent()) {
                teleportCommand.get().execute(messagePublisher);
                capturedAny |= applyCapture(mover, teleportCommand.get(), allPlayers, messagePublisher);
            }
        }

        return capturedAny;
    }

    private List<Command> collectLegalCommands(
            Player player, List<Player> allPlayers, int rollValue, Board board) {
        List<Command> legalCommands = new ArrayList<>();

        for (TurnRule rule : turnRules) {
            legalCommands.addAll(rule.findLegalCommands(player, rollValue, board, allPlayers));
        }

        return legalCommands;
    }

    // Rule 7/T-6: checks each moved piece for capture.
    private boolean applyCapture(
            Player mover, Command executedCommand, List<Player> allPlayers,
            GameMessagePublisher messagePublisher) {
        // A "cannot move" turn moves nothing, so nothing can be captured.
        if (executedCommand.getType() == CommandType.CANNOT_MOVE) {
            return false;
        }

        boolean capturedAny = false;

        for (Piece movedPiece : executedCommand.getAffectedPieces()) {
            Optional<Command> captureCommand = captureCheckRule.findCapture(mover, movedPiece, allPlayers);

            if (captureCommand.isPresent()) {
                captureCommand.get().execute(messagePublisher);
                capturedAny = true;
            }
        }

        return capturedAny;
    }

    // Rule 4/7: six or capture earns another roll.
    private boolean grantsAnotherRoll(int rollValue, boolean capturedOpponent) {
        return rollValue == DiceConstants.SIX_ROLL_VALUE || capturedOpponent;
    }
}
