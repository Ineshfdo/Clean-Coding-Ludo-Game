package message.turn;

import config.enums.PlayerColor;
import message.GameMessage;

// A player's turn begins.
public record TurnStarted(PlayerColor playerColor) implements GameMessage {
}
