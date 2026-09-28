package controller;

import java.util.List;

import message.observer.GameMessagePublisher;
import message.toss.DiceRolled;
import message.toss.TossStarting;
import message.toss.TossTied;
import message.toss.TossWon;
import model.player.Player;
import utils.dice.Dice;

// Can use this class in other classes & Can Not sub class.
// Only this class can use it, Set Once, Holds one dice.

public final class FirstPlayerSelector {

    private static final int NO_ROLL_YET = 0;

    private final Dice dice;
    private final GameMessagePublisher messagePublisher;

    /**
     * Creates the selector.
     *
     * @param dice the dice used for the toss
     * @param messagePublisher where the toss is announced
     */

    public FirstPlayerSelector(Dice dice, GameMessagePublisher messagePublisher) {
        this.dice = dice;
        this.messagePublisher = messagePublisher;
    }

    /**
     * Plays the toss and announces the rolls and the winner.
     *
     * @param tossOrder the players in the order in which they roll
     * @return the player who won the toss
     */

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