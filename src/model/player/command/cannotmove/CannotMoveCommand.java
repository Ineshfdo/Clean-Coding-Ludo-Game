package model.player.command.cannotmove;

import model.piece.Piece;
import model.player.command.MoveCommand;

// Announces why a piece or block cannot move this roll; it changes nothing on the board.
public abstract class CannotMoveCommand implements MoveCommand {

    private final Piece piece;

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
