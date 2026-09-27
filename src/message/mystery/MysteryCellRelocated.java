package message.mystery;

import message.GameMessage;

/**
 * The Mystery Cell moved to a new cell after four rounds (T-10).
 *
 * @param cellPosition the new track cell of the Mystery Cell
 */
public record MysteryCellRelocated(int cellPosition) implements GameMessage {
}
