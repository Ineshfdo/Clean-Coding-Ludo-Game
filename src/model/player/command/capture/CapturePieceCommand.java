package model.player.command.capture;

import config.enums.CommandType;
import message.GameMessage;
import message.observer.GameMessagePublisher;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;

// Rule 7: the captured piece returns to Base; the capturer gains one.
public final class CapturePieceCommand implements Command {

    private final Player capturingPlayer;
    private final Piece capturingPiece;

    private final Player capturedPlayer;
    private final Piece capturedPiece;

    public CapturePieceCommand(
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
