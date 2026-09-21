package model.player.command.cannotmove;

import config.enums.CommandType;
import message.GameMessage;
import message.observer.GameMessagePublisher;
import model.piece.Piece;
import model.player.command.Command;

// T-4: announces a block's roll dropped to zero cells.
public final class BlockRollTooSmallCommand implements Command {

    private final Piece piece;

    public BlockRollTooSmallCommand(Piece piece) {
        this.piece = piece;
    }

    @Override
    public void execute(GameMessagePublisher messagePublisher) {
        messagePublisher.publish(GameMessage.blockRollTooSmall(piece.toString()));
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
