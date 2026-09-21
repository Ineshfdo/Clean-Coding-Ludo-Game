package message.toss;

import config.enums.PlayerColor;
import message.GameMessage;

// A player rolled during the first-player toss.
public record DiceRolled(PlayerColor playerColor, int rollValue) implements GameMessage {
}
