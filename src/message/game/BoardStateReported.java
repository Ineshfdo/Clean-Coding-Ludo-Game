package message.game;

import message.GameMessage;

// The round is over: report where every piece stands.
public record BoardStateReported(int roundNumber) implements GameMessage {
}
