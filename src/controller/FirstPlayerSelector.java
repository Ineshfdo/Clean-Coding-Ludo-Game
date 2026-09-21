package controller;

import config.enums.GameMessageType;
import java.util.List;
import message.GameMessage;
import message.observer.GameMessagePublisher;
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
        messagePublisher.publish(GameMessage.of(GameMessageType.TOSS_STARTING));

        while (true) {
            Player highestRoller = tossOrder.get(0);
            int highestRoll = NO_ROLL_YET;
            int highestRollerCount = 0;

            for (Player player : tossOrder) {
                int rollValue = dice.roll();
                messagePublisher.publish(GameMessage.diceRolled(player.getColor(), rollValue));

                if (rollValue > highestRoll) {
                    highestRoll = rollValue;
                    highestRoller = player;
                    highestRollerCount = 1;
                } else if (rollValue == highestRoll) {
                    highestRollerCount++;
                }
            }

            if (highestRollerCount == 1) {
                messagePublisher.publish(GameMessage.tossWon(highestRoller.getColor(), highestRoll));
                return highestRoller;
            }

            messagePublisher.publish(GameMessage.tossTied(highestRoll));
        }
    }
}
