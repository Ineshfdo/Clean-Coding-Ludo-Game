package model.player.command.cannotmove;

import java.util.List;
import message.GameMessage;
import message.observer.GameMessagePublisher;
import model.piece.Piece;
import model.piece.PieceLabels;

// T-3: announces an opponent blockade stops this piece or block.
public final class MoveBlockedByBlockadeCommand extends CannotMoveCommand {

    private final List<Piece> blockPieces;

    public MoveBlockedByBlockadeCommand(List<Piece> blockPieces) {
        super(blockPieces.get(0));
        this.blockPieces = blockPieces;
    }

    @Override
    public void execute(GameMessagePublisher messagePublisher) {
        messagePublisher.publish(GameMessage.pieceBlocked(PieceLabels.joinPieceLabels(blockPieces)));
    }
}
