package message.move;

import message.GameMessage;

// A piece or block entered its HomeStraight.
public record PieceEnteredHomeStraight(
        String pieceLabel, String cellLabel) implements GameMessage {
}
