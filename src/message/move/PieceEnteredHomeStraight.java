package message.move;

import message.GameMessage;

/**
 * A piece or a blockade entered its HomeStraight.
 *
 * @param pieceLabel the name of the piece, or the joined names of the blockade
 * @param cellLabel the name of the HomeStraight cell that was reached
 */
public record PieceEnteredHomeStraight(
        String pieceLabel, String cellLabel) implements GameMessage {
}
