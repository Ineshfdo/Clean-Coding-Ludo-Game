package message.mystery;

import message.GameMessage;

// T-10: the Mystery Cell's first spawn, on a random empty cell.
public record MysteryCellAppeared(int cellPosition) implements GameMessage {
}
