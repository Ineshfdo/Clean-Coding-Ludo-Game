package model.player.command.cannotmove;

import java.util.List;
import message.cannotmove.PieceBlocked;
import message.observer.GameMessagePublisher;
import model.piece.Piece;
import model.piece.PieceLabels;

/**
 * Announces that an opponent blockade stops a piece or a block (T-3).
 */
public final class MoveBlockedByBlockadeCommand extends CannotMoveCommand {

    private final List<Piece> blockPieces;

    /**
     * Creates the command.
     *
     * @param blockPieces the piece or the pieces of the block that cannot move
     */
    public MoveBlockedByBlockadeCommand(List<Piece> blockPieces) {
        super(blockPieces.get(0));
        this.blockPieces = blockPieces;
    }

    @Override
    public void execute(GameMessagePublisher messagePublisher) {
        messagePublisher.publish(new PieceBlocked(PieceLabels.joinPieceLabels(blockPieces)));
    }
}
