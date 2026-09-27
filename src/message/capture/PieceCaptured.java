package message.capture;

import config.enums.PlayerColor;
import message.GameMessage;

/**
 * A piece captured an opponent piece (requirement 4). The event tells where the capture happened
 * and the new piece count of the player who lost the piece.
 *
 * @param capturingPieceLabel the name of the capturing piece
 * @param capturePosition the track cell where the capture happened
 * @param capturedPieceLabel the name of the captured piece
 * @param capturedPlayerColor the colour of the player who lost the piece
 * @param piecesOnBoard the number of pieces of that player that are now on the board
 * @param piecesAtBase the number of pieces of that player that are now at Base
 */
public record PieceCaptured(
        String capturingPieceLabel, int capturePosition, String capturedPieceLabel,
        PlayerColor capturedPlayerColor, int piecesOnBoard, int piecesAtBase) implements GameMessage {
}
