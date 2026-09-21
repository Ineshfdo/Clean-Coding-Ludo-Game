package message.capture;

import config.enums.PlayerColor;
import message.GameMessage;

// Requirement 4: reports the capture cell, the captured piece and its player's new tally.
public record PieceCaptured(
        String capturingPieceLabel, int capturePosition, String capturedPieceLabel,
        PlayerColor capturedPlayerColor, int piecesOnBoard, int piecesAtBase) implements GameMessage {
}
