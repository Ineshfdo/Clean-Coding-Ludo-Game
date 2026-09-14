package command;

import gamemessage.GameMessage;
import gamemessage.GameMessagePublisher;
import player.Piece;

// T-3: announces an opponent blockade fully preventing this piece from moving.
public final class BlockedMoveCommand implements Command {

    private final Piece piece;

    public BlockedMoveCommand(Piece piece) {
        this.piece = piece;
    }

    @Override
    public void execute(GameMessagePublisher messages) {
        messages.publish(GameMessage.pieceBlocked(piece.toString()));
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
