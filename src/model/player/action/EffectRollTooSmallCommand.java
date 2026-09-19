package model.player.action;
import config.enums.CommandType;

import service.result.GameMessage;
import view.observer.GameMessagePublisher;
import model.piece.Piece;

// T-12: announces a Sick-halved roll that fell to zero cells and cannot move.
public final class EffectRollTooSmallCommand implements Command {

    private final Piece piece;

    public EffectRollTooSmallCommand(Piece piece) {
        this.piece = piece;
    }

    @Override
    public void execute(GameMessagePublisher messages) {
        messages.publish(GameMessage.effectRollTooSmall(piece.toString()));
    }

    @Override
    public CommandType getType() {
        return CommandType.BLOCKED;
    }

    @Override
    public Piece getAffectedPiece() {
        return piece;
    }
}
