package model.player.command.capture;

import java.util.List;
import message.capture.BlockCaptured;
import message.observer.GameMessagePublisher;
import model.piece.Piece;
import model.piece.PieceLabels;
import model.player.Player;
import model.player.command.Command;

/**
 A blockade captures an opponent blockade of the same size (T-8).
 Every captured piece returns to Base, and every capturing piece gains one capture.
 */
public final class CaptureBlockCommand implements Command {

    private final Player capturingPlayer;
    private final List<Piece> capturingBlock;

    private final Player capturedPlayer;
    private final List<Piece> capturedBlock;

    /**
     Creates the command.
     @param capturingPlayer the player who captures
     @param capturingBlock the pieces of the capturing blockade
     @param capturedPlayer the player who loses the pieces
     @param capturedBlock the pieces of the captured blockade
     */
    public CaptureBlockCommand(
            Player capturingPlayer, List<Piece> capturingBlock,
            Player capturedPlayer, List<Piece> capturedBlock) {
        this.capturingPlayer = capturingPlayer;
        this.capturingBlock = capturingBlock;
        this.capturedPlayer = capturedPlayer;
        this.capturedBlock = capturedBlock;
    }

    @Override
    public void execute(GameMessagePublisher messagePublisher) {
        for (Piece capturedPiece : capturedBlock) {
            capturedPlayer.returnToBase(capturedPiece);
        }

        for (Piece capturingPiece : capturingBlock) {
            capturingPlayer.recordCapture(capturingPiece);
        }

        messagePublisher.publish(new BlockCaptured(PieceLabels.joinPieceLabels(capturingBlock), PieceLabels.joinPieceLabels(capturedBlock)));
    }

    @Override
    public Piece getAffectedPiece() {
        return capturedBlock.get(0);
    }

    @Override
    public List<Piece> getAffectedPieces() {
        return capturedBlock;
    }
}
