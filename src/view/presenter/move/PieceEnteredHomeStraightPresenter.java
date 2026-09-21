package view.presenter.move;

import message.move.PieceEnteredHomeStraight;
import view.presenter.EventPresenter;

// Wording for entering the HomeStraight.
public final class PieceEnteredHomeStraightPresenter extends EventPresenter<PieceEnteredHomeStraight> {

    public PieceEnteredHomeStraightPresenter() {
        super(PieceEnteredHomeStraight.class);
    }

    @Override
    public String present(PieceEnteredHomeStraight message) {
        return "  -> " + message.pieceLabel() + " entered its HomeStraight at " + message.cellLabel()
                + ".";
    }
}
