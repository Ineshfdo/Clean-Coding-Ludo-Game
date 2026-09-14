package turn;

import java.util.ArrayList;
import java.util.List;

import command.Command;
import dice.Dice;
import gamemessage.GameMessage;
import gamemessage.GameMessagePublisher;
import gamemessage.GameMessageType;
import ludoboard.Board;
import player.Player;
import rule.RollValidityRule;
import rule.TurnRule;
import strategy.PlayerStrategy;

// Template Method: the fixed skeleton of "play a turn" - announce
// the turn, roll, check whether the roll is voided, resolve and
// play it if not, then ask the one hook whether the player earned
// another roll. Concrete subclasses only decide that last part;
// everything else about how a turn proceeds is fixed here.
public abstract class TurnProcessor {

    private final List<TurnRule> turnRules;
    private final PlayerStrategy strategy;
    private final RollValidityRule rollValidityRule;

    protected TurnProcessor(
            List<TurnRule> turnRules, PlayerStrategy strategy, RollValidityRule rollValidityRule) {
        this.turnRules = turnRules;
        this.strategy = strategy;
        this.rollValidityRule = rollValidityRule;
    }

    public final void playTurn(
            Player player, Dice dice, Board board, GameMessagePublisher messages) {
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

            resolveAndPlay(player, rollValue, board, messages);

            turnContinues = grantsAnotherRoll(rollValue);
        }
    }

    private void resolveAndPlay(
            Player player, int rollValue, Board board, GameMessagePublisher messages) {
        List<Command> legalOptions = findLegalOptions(player, rollValue, board);

        if (legalOptions.isEmpty()) {
            messages.publish(GameMessage.noPieceMovable());
            return;
        }

        strategy.choose(legalOptions).execute(messages);
    }

    private List<Command> findLegalOptions(Player player, int rollValue, Board board) {
        List<Command> legalOptions = new ArrayList<>();
        for (TurnRule rule : turnRules) {
            rule.resolve(player, rollValue, board).ifPresent(legalOptions::add);
        }
        return legalOptions;
    }

    // Rule 4: rolling a 6 earns another roll. This one hook is
    // called after every roll, so it naturally covers both "6
    // grants a second roll" and "a second six grants a third" -
    // the template doesn't need to know which roll number it is.
    protected abstract boolean grantsAnotherRoll(int rollValue);
}
