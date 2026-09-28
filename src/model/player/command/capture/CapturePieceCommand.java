package model.player.command.capture;

import message.capture.PieceCaptured;
import message.observer.GameMessagePublisher;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;

/**
 A piece captures an opponent piece (rule 7).
 The captured piece returns to Base, and the capturing piece gains one capture.
 */
public final class CapturePieceCommand implements Command {

    private final Player capturingPlayer;
    private final Piece capturingPiece;

    private final Player capturedPlayer;
    private final Piece capturedPiece;

    /**
     Creates the command.
     @param capturingPlayer the player who captures
     @param capturingPiece the piece that landed on the opponent
     @param capturedPlayer the player who loses the piece
     @param capturedPiece the piece that is captured
     */
    public CapturePieceCommand(
            Player capturingPlayer, Piece capturingPiece,
            Player capturedPlayer, Piece capturedPiece) {
        this.capturingPlayer = capturingPlayer;
        this.capturingPiece = capturingPiece;
        this.capturedPlayer = capturedPlayer;
        this.capturedPiece = capturedPiece;
    }

    @Override
    public void execute(GameMessagePublisher messagePublisher) {
        int capturePosition = capturingPiece.getTrackPosition();

        capturedPlayer.returnToBase(capturedPiece);
        capturingPlayer.recordCapture(capturingPiece);

        messagePublisher.publish(new PieceCaptured(
                capturingPiece.toString(), capturePosition, capturedPiece.toString(),
                capturedPlayer.getColor(), capturedPlayer.countPiecesOnBoard(),
                capturedPlayer.countPiecesAtBase()));
    }

    @Override
    public Piece getAffectedPiece() {
        return capturedPiece;
    }
}
