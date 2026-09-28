package model.player.command.cannotmove;

import message.cannotmove.EffectRollTooSmall;
import message.observer.GameMessagePublisher;
import model.piece.Piece;

/**
 Announces that a Sick effect halved the roll down to zero cells (T-12).
 */
public final class SickRollTooSmallCommand extends CannotMoveCommand {

    /**
     Creates the command.
     @param piece the Sick piece that cannot move
     */
    public SickRollTooSmallCommand(Piece piece) {
        super(piece);
    }

    @Override
    public void execute(GameMessagePublisher messagePublisher) {
        messagePublisher.publish(new EffectRollTooSmall(getAffectedPiece().toString()));
    }
}
