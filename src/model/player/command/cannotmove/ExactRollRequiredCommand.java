package model.player.command.cannotmove;

import config.enums.CommandType;
import message.GameMessage;
import message.observer.GameMessagePublisher;
import model.piece.Piece;
import model.player.command.Command;

// Rule 10: announces an exact roll is needed to reach Home.
public final class ExactRollRequiredCommand implements Command {

    private final Piece piece;

    public ExactRollRequiredCommand(Piece piece) {
        this.piece = piece;
    }

    @Override
    public void execute(GameMessagePublisher messagePublisher) {
        messagePublisher.publish(GameMessage.pieceNeedsExactRoll(piece.toString()));
    }

    @Override
    public CommandType getType() {
        return CommandType.CANNOT_MOVE;
    }

    @Override
    public Piece getAffectedPiece() {
        return piece;
    }
}
