package model.player.command.capture;
import model.player.command.Command;
import config.enums.CommandType;

import service.result.GameMessage;
import view.observer.GameMessagePublisher;
import model.piece.Piece;
import model.player.Player;

// Rule 7 as a command: sends a captured piece to Base and credits the capturer.
public final class CaptureCommand implements Command {

    private final Player capturingPlayer;
    private final Piece capturingPiece;
    private final Player capturedPlayer;
    private final Piece capturedPiece;

    public CaptureCommand(
            Player capturingPlayer, Piece capturingPiece,
            Player capturedPlayer, Piece capturedPiece) {
        this.capturingPlayer = capturingPlayer;
        this.capturingPiece = capturingPiece;
        this.capturedPlayer = capturedPlayer;
        this.capturedPiece = capturedPiece;
    }

    @Override
    public void execute(GameMessagePublisher messages) {
        int capturePosition = capturingPiece.getTrackPosition();
        capturedPlayer.returnToBase(capturedPiece);
        capturingPlayer.recordCapture(capturingPiece);
        messages.publish(GameMessage.pieceCaptured(
                capturingPiece.toString(), capturePosition, capturedPiece.toString(),
                capturedPlayer.getColor(), capturedPlayer.countPiecesOnBoard(),
                capturedPlayer.countPiecesAtBase()));
    }

    @Override
    public CommandType getType() {
        return CommandType.CAPTURE;
    }

    @Override
    public Piece getAffectedPiece() {
        return capturedPiece;
    }
}
