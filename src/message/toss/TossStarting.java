package message.toss;

import message.GameMessage;

// Everyone is about to roll for the first turn.
public record TossStarting() implements GameMessage {
}
