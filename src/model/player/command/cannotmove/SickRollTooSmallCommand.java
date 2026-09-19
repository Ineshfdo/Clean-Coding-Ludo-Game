package model.player.command.cannotmove;

import config.enums.CommandType;
import message.GameMessage;
import model.piece.Piece;
import model.player.command.Command;
import view.observer.GameMessagePublisher;

// T-12: announces a Sick roll dropped to zero cells.
public final class SickRollTooSmallCommand implements Command {

    private final Piece piece;

    public SickRollTooSmallCommand(Piece piece) {
        this.piece = piece;
    }

    @Override
    public void execute(GameMessagePublisher messages) {
        messages.publish(GameMessage.effectRollTooSmall(piece.toString()));
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
