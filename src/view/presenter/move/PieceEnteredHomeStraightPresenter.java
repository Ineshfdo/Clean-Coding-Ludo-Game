package view.presenter.move;

import message.move.PieceEnteredHomeStraight;
import view.presenter.EventPresenter;

/**
 * Presents {@link PieceEnteredHomeStraight}: it tells that a piece or a blockade entered its
 * HomeStraight.
 */
public final class PieceEnteredHomeStraightPresenter extends EventPresenter<PieceEnteredHomeStraight> {

    /**
     * Creates the presenter for {@link PieceEnteredHomeStraight}.
     */
    public PieceEnteredHomeStraightPresenter() {
        super(PieceEnteredHomeStraight.class);
    }

    @Override
    public String present(PieceEnteredHomeStraight message) {
        return "  -> " + message.pieceLabel() + " entered its HomeStraight at " + message.cellLabel()
                + ".";
    }
}
