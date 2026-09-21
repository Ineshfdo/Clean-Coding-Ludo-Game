package model.player.command.cannotmove;

import message.cannotmove.PieceNeedsExactRoll;
import message.observer.GameMessagePublisher;
import model.piece.Piece;

// Rule 10: announces an exact roll is needed to reach Home.
public final class ExactRollRequiredCommand extends CannotMoveCommand {

    public ExactRollRequiredCommand(Piece piece) {
        super(piece);
    }

    @Override
    public void execute(GameMessagePublisher messagePublisher) {
        messagePublisher.publish(new PieceNeedsExactRoll(getAffectedPiece().toString()));
    }
}
