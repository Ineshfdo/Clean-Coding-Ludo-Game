package turn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import command.Command;
import dice.Dice;
import gamemessage.GameMessage;
import gamemessage.GameMessagePublisher;
import gamemessage.GameMessageType;
import ludoboard.Board;
import player.Piece;
import player.Player;
import rule.BetaRestrictionRule;
import rule.BlockadeBreakRule;
import rule.CaptureRule;
import rule.MysteryCellTeleportRule;
import rule.RollValidityRule;
import rule.TurnRule;
import strategy.PlayerStrategy;

// Template Method: the fixed skeleton for playing a turn;
// subclasses only decide one hook.
public abstract class TurnProcessor {

    private final List<TurnRule> turnRules;
    private final PlayerStrategy strategy;
    private final RollValidityRule rollValidityRule;
    private final CaptureRule captureRule;
    private final BlockadeBreakRule blockadeBreakRule;
    private final MysteryCellTeleportRule mysteryCellTeleportRule;
    private final BetaRestrictionRule betaRestrictionRule;

    protected TurnProcessor(
            List<TurnRule> turnRules, PlayerStrategy strategy,
            RollValidityRule rollValidityRule, CaptureRule captureRule,
            BlockadeBreakRule blockadeBreakRule, MysteryCellTeleportRule mysteryCellTeleportRule,
            BetaRestrictionRule betaRestrictionRule) {
        this.turnRules = turnRules;
        this.strategy = strategy;
        this.rollValidityRule = rollValidityRule;
        this.captureRule = captureRule;
        this.blockadeBreakRule = blockadeBreakRule;
        this.mysteryCellTeleportRule = mysteryCellTeleportRule;
        this.betaRestrictionRule = betaRestrictionRule;
    }

    public final void playTurn(
            Player player, List<Player> allPlayers, Dice dice, Board board,
            GameMessagePublisher messages) {
        messages.publish(GameMessage.turnStarted(player.getColor()));

        int rollNumber = 0;
        boolean turnContinues = true;

        while (turnContinues) {
            rollNumber++;
            int rollValue = dice.roll();
            messages.publish(GameMessage.turnRolled(player.getColor(), rollValue));

            Optional<Command> forcedBreak =
                    blockadeBreakRule.resolve(player, rollNumber, rollValue, board, allPlayers);
            if (forcedBreak.isPresent()) {
                forcedBreak.get().execute(messages);
                applyCapture(player, forcedBreak.get(), allPlayers, messages);
                return;
            }

            if (rollValidityRule.isVoided(rollNumber, rollValue)) {
                messages.publish(GameMessage.of(GameMessageType.THIRD_SIX_VOIDED));
                return;
            }

            // T-13: this round's roll may force a still-Beta-restricted piece/block back to Base.
            betaRestrictionRule.resolve(player, rollNumber, rollValue)
                    .ifPresent(command -> command.execute(messages));

            boolean capturedOpponent = resolveAndPlay(player, allPlayers, rollValue, board, messages);

            turnContinues = grantsAnotherRoll(rollValue, capturedOpponent);
        }
    }

    private boolean resolveAndPlay(
            Player player, List<Player> allPlayers, int rollValue, Board board,
            GameMessagePublisher messages) {
        List<Command> legalOptions = findLegalOptions(player, allPlayers, rollValue, board);

        if (legalOptions.isEmpty()) {
            messages.publish(GameMessage.noPieceMovable());
            return false;
        }

        Command chosenCommand = strategy.choose(legalOptions);
        chosenCommand.execute(messages);

        boolean capturedOpponent = applyCapture(player, chosenCommand, allPlayers, messages);
        applyMysteryCellTeleport(player, chosenCommand, messages);
        return capturedOpponent;
    }

    // T-11: every distinct landing position among the moved pieces is checked once for the Mystery Cell.
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
            rule.resolve(player, rollValue, board, allPlayers).ifPresent(legalOptions::add);
        }
        return legalOptions;
    }

    // Rule 7/T-6: every piece an executed command actually moved is checked for a capture.
    private boolean applyCapture(
            Player mover, Command executedCommand, List<Player> allPlayers,
            GameMessagePublisher messages) {
        boolean capturedAny = false;
        for (Piece movedPiece : executedCommand.getAffectedPieces()) {
            Optional<Command> captureCommand = captureRule.resolve(mover, movedPiece, allPlayers);
            if (captureCommand.isPresent()) {
                captureCommand.get().execute(messages);
                capturedAny = true;
            }
        }
        return capturedAny;
    }

    // Rule 4/7: a 6 or a capture earns another roll; called
    // after every roll automatically.
    protected abstract boolean grantsAnotherRoll(int rollValue, boolean capturedOpponent);
}
