package command;

import gamemessage.GameMessage;
import gamemessage.GameMessagePublisher;
import player.Piece;
import player.Player;

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
        capturedPlayer.returnToBase(capturedPiece);
        capturingPlayer.recordCapture();
        messages.publish(GameMessage.pieceCaptured(capturingPiece.toString(), capturedPiece.toString()));
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
