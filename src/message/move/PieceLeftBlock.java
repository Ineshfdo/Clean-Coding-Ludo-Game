package message.move;

import message.GameMessage;

// A piece left its block and resumes its own direction.
public record PieceLeftBlock(String pieceLabel) implements GameMessage {
}
