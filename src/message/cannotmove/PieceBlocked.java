package message.cannotmove;

import message.GameMessage;

/**
 An opponent's blockade stops a piece or a block (T-3).
 @param pieceLabel the name of the piece, or the joined names of the pieces of the block
 */
public record PieceBlocked(String pieceLabel) implements GameMessage {
}
