package model.player.command.cannotmove;

import message.cannotmove.BlockRollTooSmall;
import message.observer.GameMessagePublisher;
import model.piece.Piece;

/**
 Announces that the roll of a block was divided down to zero cells (T-4).
 */
public final class BlockRollTooSmallCommand extends CannotMoveCommand {

    /**
     Creates the command.
     @param piece a piece of the block that cannot move
     */
    public BlockRollTooSmallCommand(Piece piece) {
        super(piece);
    }

    @Override
    public void execute(GameMessagePublisher messagePublisher) {
        messagePublisher.publish(new BlockRollTooSmall(getAffectedPiece().toString()));
    }
}
