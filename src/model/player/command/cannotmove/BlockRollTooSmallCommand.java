package model.player.command.cannotmove;

import message.GameMessage;
import message.observer.GameMessagePublisher;
import model.piece.Piece;

// T-4: announces a block's roll dropped to zero cells.
public final class BlockRollTooSmallCommand extends CannotMoveCommand {

    public BlockRollTooSmallCommand(Piece piece) {
        super(piece);
    }

    @Override
    public void execute(GameMessagePublisher messagePublisher) {
        messagePublisher.publish(GameMessage.blockRollTooSmall(getAffectedPiece().toString()));
    }
}
