package message.toss;

import config.enums.PlayerColor;
import message.GameMessage;

/**
 * A player won the toss and goes first.
 *
 * @param playerColor the colour of the winner
 * @param rollValue the winning roll
 */
public record TossWon(PlayerColor playerColor, int rollValue) implements GameMessage {
}
