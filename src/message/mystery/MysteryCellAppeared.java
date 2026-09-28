package message.mystery;

import message.GameMessage;

/**
 The Mystery Cell appeared for the first time, on a random empty cell (T-10).
 @param cellPosition the track cell of the Mystery Cell
 */
public record MysteryCellAppeared(int cellPosition) implements GameMessage {
}
