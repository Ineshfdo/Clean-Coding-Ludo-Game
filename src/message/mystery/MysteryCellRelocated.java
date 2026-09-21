package message.mystery;

import message.GameMessage;

// T-10: the Mystery Cell relocates after four rounds.
public record MysteryCellRelocated(int cellPosition) implements GameMessage {
}
