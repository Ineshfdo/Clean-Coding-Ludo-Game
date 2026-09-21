package message.cannotmove;

import message.GameMessage;

// T-3: an opponent's blockade stops this piece or block.
public record PieceBlocked(String pieceLabel) implements GameMessage {
}
