package controller;

import config.constant.DiceConstants;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import message.observer.GameMessagePublisher;
import message.turn.NoPieceMovable;
import message.turn.ThirdSixVoided;
import message.turn.TurnRolled;
import message.turn.TurnStarted;
import model.board.Board;
import model.effect.mysterycell.MysteryCellLocation;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.command.MoveCommand;
import model.player.rule.block.BlockadeBreakRule;
import model.player.rule.capture.CaptureCheckRule;
import model.player.rule.mystery.TeleportRule;
import model.player.rule.roll.RollEvent;
import model.player.rule.roll.RollHook;
import model.player.rule.roll.RollValidityRule;
import model.player.rule.turn.TurnRule;
import model.player.strategy.PlayerStrategy;
import model.player.strategy.PlayerStrategyLookup;
import model.player.strategy.StrategyContext;
import utils.dice.Dice;

public final class GameEngine {

    private final List<TurnRule> turnRules;
    private final PlayerStrategyLookup strategyLookup;
    private final RollValidityRule rollValidityRule;
    private final CaptureCheckRule captureCheckRule;
    private final BlockadeBreakRule blockadeBreakRule;
    private final TeleportRule teleportRule;
    private final MysteryCellLocation mysteryCellLocation;
    private final List<RollHook> rollHooks;

    public GameEngine(
            List<TurnRule> turnRules, PlayerStrategyLookup strategyLookup,
            RollValidityRule rollValidityRule, CaptureCheckRule captureCheckRule,
            BlockadeBreakRule blockadeBreakRule, TeleportRule teleportRule,
            MysteryCellLocation mysteryCellLocation, List<RollHook> rollHooks) {
        this.turnRules = turnRules;
        this.strategyLookup = strategyLookup;
        this.rollValidityRule = rollValidityRule;
        this.captureCheckRule = captureCheckRule;
        this.blockadeBreakRule = blockadeBreakRule;
        this.teleportRule = teleportRule;
        this.mysteryCellLocation = mysteryCellLocation;
        this.rollHooks = List.copyOf(rollHooks);
    }

    public void playTurn(
            Player player, List<Player> allPlayers, Dice dice, Board board,
            GameMessagePublisher messagePublisher) {
        messagePublisher.publish(new TurnStarted(player.getColor()));

        int rollNumber = 0;

        // Tracks consecutive sixes; resets on any non-six roll.
        int consecutiveSixCount = 0;
        boolean turnContinues = true;

        while (turnContinues) {
            rollNumber++;
            int rollValue = dice.roll();
            consecutiveSixCount = rollValue == DiceConstants.SIX_ROLL_VALUE ? consecutiveSixCount + 1 : 0;

            messagePublisher.publish(new TurnRolled(player.getColor(), rollValue));

            Optional<Command> forcedBreak =
                    blockadeBreakRule.findForcedBreak(player, consecutiveSixCount, rollValue, board, allPlayers);
            if (forcedBreak.isPresent()) {
                forcedBreak.get().execute(messagePublisher);
                applyCapture(player, forcedBreak.get(), allPlayers, messagePublisher);
                return;
            }

            if (rollValidityRule.isVoided(consecutiveSixCount, rollValue)) {
                messagePublisher.publish(new ThirdSixVoided());
                return;
            }

            // Runs before legal commands, so strategy previews and the real move agree.
            runRollHooks(new RollEvent(player, allPlayers, rollNumber, rollValue), messagePublisher);

            boolean capturedOpponent =
                    resolveAndPlay(player, allPlayers, rollNumber, rollValue, board, messagePublisher);

            turnContinues = grantsAnotherRoll(rollValue, capturedOpponent);
        }
    }

    // T-7/T-13: bookkeeping after an accepted roll, e.g. the home gate and the Beta restriction.
    private void runRollHooks(RollEvent roll, GameMessagePublisher messagePublisher) {
        for (RollHook rollHook : rollHooks) {
            rollHook.onRollAccepted(roll, messagePublisher);
        }
    }

    private boolean resolveAndPlay(
            Player player, List<Player> allPlayers, int rollNumber, int rollValue, Board board,
            GameMessagePublisher messagePublisher) {
        List<MoveCommand> legalCommands = collectLegalCommands(player, allPlayers, rollValue, board);

        if (legalCommands.isEmpty()) {
            messagePublisher.publish(new NoPieceMovable());
            return false;
        }

        PlayerStrategy strategy = strategyLookup.getStrategyFor(player.getColor());
        StrategyContext context = new StrategyContext(
                player, allPlayers, board, mysteryCellLocation, rollNumber);

        MoveCommand chosenCommand = strategy.choose(legalCommands, context);
        chosenCommand.execute(messagePublisher);

        // A "cannot move" turn moves nothing, so nothing can be captured.
        boolean capturedOpponent = !chosenCommand.movesNothing()
                && applyCapture(player, chosenCommand, allPlayers, messagePublisher);
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

            Optional<Command> teleportCommand = teleportRule.findTeleport(mover, movedPiece);

            if (teleportCommand.isPresent()) {
                teleportCommand.get().execute(messagePublisher);
                capturedAny |= applyCapture(mover, teleportCommand.get(), allPlayers, messagePublisher);
            }
        }

        return capturedAny;
    }

    private List<MoveCommand> collectLegalCommands(
            Player player, List<Player> allPlayers, int rollValue, Board board) {
        List<MoveCommand> legalCommands = new ArrayList<>();

        for (TurnRule rule : turnRules) {
            legalCommands.addAll(rule.findLegalCommands(player, rollValue, board, allPlayers));
        }

        return legalCommands;
    }

    // Rule 7/T-6: checks each moved piece for capture.
    private boolean applyCapture(
            Player mover, Command executedCommand, List<Player> allPlayers,
            GameMessagePublisher messagePublisher) {
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
