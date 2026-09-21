package message.move;

import config.enums.PlayerColor;
import message.GameMessage;

// Observer: reports the piece that left Base and the player's board/base tally.
public record PieceEnteredBoard(
        PlayerColor playerColor, String pieceLabel, int newPosition, int piecesOnBoard,
        int piecesAtBase) implements GameMessage {
}
