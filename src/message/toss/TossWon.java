package message.toss;

import config.enums.PlayerColor;
import message.GameMessage;

// A player won the toss and goes first.
public record TossWon(PlayerColor playerColor, int rollValue) implements GameMessage {
}
