package message.cannotmove;

import message.GameMessage;

// T-4: a block's roll dropped to zero cells.
public record BlockRollTooSmall(String pieceLabel) implements GameMessage {
}
