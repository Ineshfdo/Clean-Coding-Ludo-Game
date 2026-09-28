package model.player.command.cannotmove;

import model.piece.Piece;
import model.player.command.MoveCommand;

/**
 Base class of the commands that only announce why a piece or a block cannot move.
 They change nothing on the board.
 */
public abstract class CannotMoveCommand implements MoveCommand {

    private final Piece piece;

    /**
     Creates the command.
     @param piece the piece that cannot move, or the first piece of the block that cannot move
     */
    protected CannotMoveCommand(Piece piece) {
        this.piece = piece;
    }

    @Override
    public final Piece getAffectedPiece() {
        return piece;
    }

    @Override
    public final boolean movesNothing() {
        return true;
    }
}
