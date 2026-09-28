package message.move;

import message.GameMessage;

/**
 A piece left its blockade and resumes its own direction (T-5).
 @param pieceLabel the name of the piece
 */
public record PieceLeftBlock(String pieceLabel) implements GameMessage {
}
