package message.game;

import message.GameMessage;

/**
 * A new round begins.
 *
 * @param roundNumber the number of the round, counted from 1
 */
public record RoundStarted(int roundNumber) implements GameMessage {
}
