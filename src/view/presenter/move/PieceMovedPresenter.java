package view.presenter.move;

import message.move.PieceMoved;
import model.board.Board;
import utils.color.PlayerColorNames;
import view.presenter.CellNames;
import view.presenter.EventPresenter;

/**
 Presents {@link PieceMoved}: it tells the move of a single piece: the number of steps, the direction and both cells.
 */
public final class PieceMovedPresenter extends EventPresenter<PieceMoved> {

    private final CellNames cellNames;

    /**
     Creates the presenter.
     @param board gives the Approach cells, so that they get their own name
     */
    public PieceMovedPresenter(Board board) {
        super(PieceMoved.class);
        this.cellNames = new CellNames(board);
    }

    @Override
    public String present(PieceMoved message) {
        return "  -> " + PlayerColorNames.displayNameOf(message.playerColor()) + " moves piece "
                + message.pieceLabel()
                + " from location " + cellNames.labelOf(message.fromPosition())
                + " to " + cellNames.labelOf(message.newPosition())
                + " by " + message.rollValue() + " units in "
                + message.movementDirectionLabel() + " direction.";
    }
}
