package model.player.command.cannotmove;

import config.enums.CommandType;
import message.GameMessage;
import model.piece.Piece;
import model.player.command.Command;
import view.observer.GameMessagePublisher;

// Rule 10: announces an exact roll is needed to reach Home.
public final class ExactRollRequiredCommand implements Command {

    private final Piece piece;

    public ExactRollRequiredCommand(Piece piece) {
        this.piece = piece;
    }

    @Override
    public void execute(GameMessagePublisher messages) {
        messages.publish(GameMessage.pieceNeedsExactRoll(piece.toString()));
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
