package model.player.command.cannotmove;

import config.enums.CommandType;
import model.piece.Piece;
import model.player.command.Command;
import service.result.GameMessage;
import view.observer.GameMessagePublisher;

// T-4: announces a block's roll dropped to zero cells.
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
        return CommandType.CANNOT_MOVE;
    }

    @Override
    public Piece getAffectedPiece() {
        return piece;
    }
}
