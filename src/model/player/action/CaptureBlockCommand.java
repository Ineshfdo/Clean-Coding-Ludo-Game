package model.player.action;
import config.enums.CommandType;

import java.util.List;
import java.util.stream.Collectors;

import service.result.GameMessage;
import view.observer.GameMessagePublisher;
import model.piece.Piece;
import model.player.Player;

// T-8: an equal-sized blockade captures the opposing blockade; every capturing piece gains one.
public final class CaptureBlockCommand implements Command {

    private final Player capturingPlayer;
    private final List<Piece> capturingBlock;
    private final Player capturedPlayer;
    private final List<Piece> capturedBlock;

    public CaptureBlockCommand(
            Player capturingPlayer, List<Piece> capturingBlock,
            Player capturedPlayer, List<Piece> capturedBlock) {
        this.capturingPlayer = capturingPlayer;
        this.capturingBlock = capturingBlock;
        this.capturedPlayer = capturedPlayer;
        this.capturedBlock = capturedBlock;
    }

    @Override
    public void execute(GameMessagePublisher messages) {
        for (Piece capturedPiece : capturedBlock) {
            capturedPlayer.returnToBase(capturedPiece);
        }
        for (Piece capturingPiece : capturingBlock) {
            capturingPlayer.recordCapture(capturingPiece);
        }
        messages.publish(GameMessage.blockCaptured(describeBlock(capturingBlock), describeBlock(capturedBlock)));
    }

    private static String describeBlock(List<Piece> block) {
        return block.stream().map(Piece::toString).collect(Collectors.joining("+"));
    }

    @Override
    public CommandType getType() {
        return CommandType.CAPTURE;
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
