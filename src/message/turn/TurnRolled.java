package message.turn;

import config.enums.PlayerColor;
import message.GameMessage;

/**
 * A player rolled the die during a turn.
 *
 * @param playerColor the colour of the player
 * @param rollValue the value of the roll
 */
public record TurnRolled(PlayerColor playerColor, int rollValue) implements GameMessage {
}
