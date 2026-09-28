package message.turn;

import config.enums.PlayerColor;
import message.GameMessage;

/**
 The turn of a player begins.
 @param playerColor the colour of the player
 */
public record TurnStarted(PlayerColor playerColor) implements GameMessage {
}
