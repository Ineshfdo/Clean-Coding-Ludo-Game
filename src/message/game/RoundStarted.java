package message.game;

import message.GameMessage;

// A new round begins.
public record RoundStarted(int roundNumber) implements GameMessage {
}
