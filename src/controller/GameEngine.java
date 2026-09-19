package controller;

import config.constant.DiceConstants;
import config.enums.GameMessageType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import service.result.GameMessage;
import utils.random.Dice;
import view.observer.GameMessagePublisher;

public final class GameEngine {

    private final List<TurnRule> turnRules;
    private final PlayerStrategyRegistry strategyRegistry;
    private final RollValidityRule rollValidityRule;
    private final CaptureCheckRule captureCheckRule;
    private final BlockadeBreakRule blockadeBreakRule;
    private final MysteryCellTeleportRule mysteryCellTeleportRule;
    private final BetaRestrictionRule betaRestrictionRule;

    public GameEngine(
            List<TurnRule> turnRules, PlayerStrategyRegistry strategyRegistry,
            RollValidityRule rollValidityRule, CaptureCheckRule captureCheckRule,
            BlockadeBreakRule blockadeBreakRule, MysteryCellTeleportRule mysteryCellTeleportRule,
            BetaRestrictionRule betaRestrictionRule) {
        this.turnRules = turnRules;
        this.strategyRegistry = strategyRegistry;
        this.rollValidityRule = rollValidityRule;
        this.captureCheckRule = captureCheckRule;
        this.blockadeBreakRule = blockadeBreakRule;
        this.mysteryCellTeleportRule = mysteryCellTeleportRule;
        this.betaRestrictionRule = betaRestrictionRule;
    }

    public void playTurn(
            Player player, List<Player> allPlayers, Dice dice, Board board,
            GameMessagePublisher messages) {
        messages.publish(GameMessage.turnStarted(player.getColor()));

        int rollNumber = 0;

        // Tracks consecutive sixes; resets on any non-six roll.
        int consecutiveSixCount = 0;
        boolean turnContinues = true;

        while (turnContinues) {
            rollNumber++;
            int rollValue = dice.roll();
            consecutiveSixCount = rollValue == DiceConstants.SIX_ROLL_VALUE ? consecutiveSixCount + 1 : 0;

            messages.publish(GameMessage.turnRolled(player.getColor(), rollValue));

            Optional<Command> forcedBreak =
                    blockadeBreakRule.resolve(player, consecutiveSixCount, rollValue, board, allPlayers);
            if (forcedBreak.isPresent()) {
                forcedBreak.get().execute(messages);
                applyCapture(player, forcedBreak.get(), allPlayers, messages);
                return;
            }

            if (rollValidityRule.isVoided(consecutiveSixCount, rollValue)) {
                messages.publish(GameMessage.of(GameMessageType.THIRD_SIX_VOIDED));
                return;
            }

            // T-13: forces still-restricted Beta piece back to Base.
            betaRestrictionRule.resolve(player, rollNumber, rollValue)
                    .ifPresent(command -> command.execute(messages));

            boolean capturedOpponent =
                    resolveAndPlay(player, allPlayers, rollNumber, rollValue, board, messages);

            turnContinues = grantsAnotherRoll(rollValue, capturedOpponent);
        }
    }

    private boolean resolveAndPlay(
            Player player, List<Player> allPlayers, int rollNumber, int rollValue, Board board,
            GameMessagePublisher messages) {
        List<Command> legalOptions = findLegalOptions(player, allPlayers, rollValue, board);

        if (legalOptions.isEmpty()) {
            messages.publish(GameMessage.noPieceMovable());
            return false;
        }

        PlayerStrategy strategy = strategyRegistry.getStrategyFor(player.getColor());
        StrategyContext context = new StrategyContext(
                player, allPlayers, board, mysteryCellTeleportRule.getMysteryCellLocation(), rollNumber);

        Command chosenCommand = strategy.choose(legalOptions, context);
        chosenCommand.execute(messages);

        boolean capturedOpponent = applyCapture(player, chosenCommand, allPlayers, messages);
        applyMysteryCellTeleport(player, chosenCommand, messages);

        return capturedOpponent;
    }

    // T-11: checks distinct landing positions for Mystery Cell.
    private void applyMysteryCellTeleport(
            Player mover, Command executedCommand, GameMessagePublisher messages) {
        Set<Integer> checkedPositions = new HashSet<>();

        for (Piece movedPiece : executedCommand.getAffectedPieces()) {
            if (!movedPiece.isOnTrack() || !checkedPositions.add(movedPiece.getTrackPosition())) {
                continue;
            }

            mysteryCellTeleportRule.resolve(mover, movedPiece)
                .ifPresent(teleportCommand -> teleportCommand.execute(messages));
        }
    }

    private List<Command> findLegalOptions(
            Player player, List<Player> allPlayers, int rollValue, Board board) {
        List<Command> legalOptions = new ArrayList<>();

        for (TurnRule rule : turnRules) {
            legalOptions.addAll(rule.resolve(player, rollValue, board, allPlayers));
        }

        return legalOptions;
    }

    // Rule 7/T-6: checks each moved piece for capture.
    private boolean applyCapture(
            Player mover, Command executedCommand, List<Player> allPlayers,
            GameMessagePublisher messages) {
        boolean capturedAny = false;

        for (Piece movedPiece : executedCommand.getAffectedPieces()) {
            Optional<Command> captureCommand = captureCheckRule.resolve(mover, movedPiece, allPlayers);

            if (captureCommand.isPresent()) {
                captureCommand.get().execute(messages);
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
