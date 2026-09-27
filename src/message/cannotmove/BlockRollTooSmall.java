package message.cannotmove;

import message.GameMessage;

/**
 * A block's roll was divided down to zero cells, so the block cannot move (T-4).
 *
 * @param pieceLabel the name of the piece that stands for the block
 */
public record BlockRollTooSmall(String pieceLabel) implements GameMessage {
}
