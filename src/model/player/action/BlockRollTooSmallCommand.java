package model.player.action;
import config.enums.CommandType;

import service.result.GameMessage;
import view.observer.GameMessagePublisher;
import model.piece.Piece;

// T-4: announces a block's roll divided down to zero cells.
public final class BlockRollTooSmallCommand implements Command {

    private final Piece piece;

    public BlockRollTooSmallCommand(Piece piece) {
        this.piece = piece;
    }

    @Override
    public void execute(GameMessagePublisher messages) {
        messages.publish(GameMessage.blockRollTooSmall(piece.toString()));
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
