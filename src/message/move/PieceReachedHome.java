package message.move;

import message.GameMessage;

// A piece or block reached Home and is removed from play.
public record PieceReachedHome(String pieceLabel) implements GameMessage {
}
