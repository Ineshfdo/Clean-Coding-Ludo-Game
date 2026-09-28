package model.player.command.cannotmove;

import message.cannotmove.PieceNeedsExactRoll;
import message.observer.GameMessagePublisher;
import model.piece.Piece;

/**
 Announces that an exact roll is needed to reach Home (rule 10).
 */
public final class ExactRollRequiredCommand extends CannotMoveCommand {

    /**
     Creates the command.
     @param piece the piece on the HomeStraight that cannot move
     */
    public ExactRollRequiredCommand(Piece piece) {
        super(piece);
    }

    @Override
    public void execute(GameMessagePublisher messagePublisher) {
        messagePublisher.publish(new PieceNeedsExactRoll(getAffectedPiece().toString()));
    }
}
