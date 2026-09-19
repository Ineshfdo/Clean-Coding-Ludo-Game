package model.player.command.capture;

import config.enums.CommandType;
import java.util.List;
import java.util.stream.Collectors;
import message.GameMessage;
import message.observer.GameMessagePublisher;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;

// T-8: a blockade captures an equal-sized blockade; each capturer gains one.
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
