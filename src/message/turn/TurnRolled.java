package message.turn;

import config.enums.PlayerColor;
import message.GameMessage;

// A player rolled the dice during a turn.
public record TurnRolled(PlayerColor playerColor, int rollValue) implements GameMessage {
}
