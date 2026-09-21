package view.presenter.move;

import message.move.PieceMoved;
import model.board.Board;
import utils.color.PlayerColorNames;
import view.presenter.CellNames;
import view.presenter.EventPresenter;

// Wording for a solo move: the dice value, the direction and both endpoints.
public final class PieceMovedPresenter extends EventPresenter<PieceMoved> {

    private final CellNames cellNames;

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
