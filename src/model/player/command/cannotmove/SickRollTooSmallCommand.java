package model.player.command.cannotmove;

import message.cannotmove.EffectRollTooSmall;
import message.observer.GameMessagePublisher;
import model.piece.Piece;

// T-12: announces a Sick roll dropped to zero cells.
public final class SickRollTooSmallCommand extends CannotMoveCommand {

    public SickRollTooSmallCommand(Piece piece) {
        super(piece);
    }

    @Override
    public void execute(GameMessagePublisher messagePublisher) {
        messagePublisher.publish(new EffectRollTooSmall(getAffectedPiece().toString()));
    }
}
