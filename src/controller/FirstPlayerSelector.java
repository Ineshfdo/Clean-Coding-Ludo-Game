package controller;

import java.util.List;
import message.observer.GameMessagePublisher;
import message.toss.DiceRolled;
import message.toss.TossStarting;
import message.toss.TossTied;
import message.toss.TossWon;
import model.player.Player;
import utils.dice.Dice;

// Rule 3.2: everyone rolls once to see who goes first; a tie for highest rerolls everyone.
public final class FirstPlayerSelector {

    private static final int NO_ROLL_YET = 0;

    private final Dice dice;
    private final GameMessagePublisher messagePublisher;

    public FirstPlayerSelector(Dice dice, GameMessagePublisher messagePublisher) {
        this.dice = dice;
        this.messagePublisher = messagePublisher;
    }

    public Player select(List<Player> tossOrder) {
        messagePublisher.publish(new TossStarting());

        while (true) {
            Player highestRoller = tossOrder.get(0);
            int highestRoll = NO_ROLL_YET;
            int highestRollerCount = 0;

            for (Player player : tossOrder) {
                int rollValue = dice.roll();
                messagePublisher.publish(new DiceRolled(player.getColor(), rollValue));

                if (rollValue > highestRoll) {
                    highestRoll = rollValue;
                    highestRoller = player;
                    highestRollerCount = 1;
                } else if (rollValue == highestRoll) {
                    highestRollerCount++;
                }
            }

            if (highestRollerCount == 1) {
                messagePublisher.publish(new TossWon(highestRoller.getColor(), highestRoll));
                return highestRoller;
            }

            messagePublisher.publish(new TossTied(highestRoll));
        }
    }
}
