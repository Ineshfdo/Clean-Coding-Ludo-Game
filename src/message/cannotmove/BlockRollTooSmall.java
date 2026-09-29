package message.cannotmove;

import message.GameMessage;

/**
A block's roll was divided down to zero cells, so the block cannot move (T-4).
Record: Immutable (Data that can't be changed after it's created) no manual getter needed
Implements GameMessage: lets code handle all message types the same way
@param pieceLabel the name of the piece that stands for the block
*/

public record BlockRollTooSmall(String pieceLabel) implements GameMessage {}