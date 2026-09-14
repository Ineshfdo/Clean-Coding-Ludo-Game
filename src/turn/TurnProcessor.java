package turn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import command.Command;
import dice.Dice;
import gamemessage.GameMessage;
import gamemessage.GameMessagePublisher;
import gamemessage.GameMessageType;
import ludoboard.Board;
import player.Piece;
import player.Player;
import rule.CaptureRule;
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

    protected TurnProcessor(
            List<TurnRule> turnRules, PlayerStrategy strategy,
            RollValidityRule rollValidityRule, CaptureRule captureRule) {
        this.turnRules = turnRules;
        this.strategy = strategy;
        this.rollValidityRule = rollValidityRule;
        this.captureRule = captureRule;
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

            if (rollValidityRule.isVoided(rollNumber, rollValue)) {
                messages.publish(GameMessage.of(GameMessageType.THIRD_SIX_VOIDED));
                return;
            }

            boolean capturedOpponent = resolveAndPlay(player, allPlayers, rollValue, board, messages);

            turnContinues = grantsAnotherRoll(rollValue, capturedOpponent);
        }
    }

    private boolean resolveAndPlay(
            Player player, List<Player> allPlayers, int rollValue, Board board,
            GameMessagePublisher messages) {
        List<Command> legalOptions = findLegalOptions(player, rollValue, board);

        if (legalOptions.isEmpty()) {
            messages.publish(GameMessage.noPieceMovable());
            return false;
        }

        Command chosenCommand = strategy.choose(legalOptions);
        chosenCommand.execute(messages);

        return applyCapture(player, chosenCommand.getAffectedPiece(), allPlayers, messages);
    }

    private List<Command> findLegalOptions(Player player, int rollValue, Board board) {
        List<Command> legalOptions = new ArrayList<>();
        for (TurnRule rule : turnRules) {
            rule.resolve(player, rollValue, board).ifPresent(legalOptions::add);
        }
        return legalOptions;
    }

    // Rule 7: a piece landing on an opponent's cell captures it.
    private boolean applyCapture(
            Player mover, Piece movedPiece, List<Player> allPlayers,
            GameMessagePublisher messages) {
        Optional<Command> captureCommand = captureRule.resolve(mover, movedPiece, allPlayers);
        captureCommand.ifPresent(command -> command.execute(messages));
        return captureCommand.isPresent();
    }

    // Rule 4/7: a 6 or a capture earns another roll; called
    // after every roll automatically.
    protected abstract boolean grantsAnotherRoll(int rollValue, boolean capturedOpponent);
}
