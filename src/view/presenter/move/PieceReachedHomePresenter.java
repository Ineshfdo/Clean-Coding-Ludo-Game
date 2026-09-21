package view.presenter.move;

import message.move.PieceReachedHome;
import view.presenter.EventPresenter;

// Wording for reaching Home.
public final class PieceReachedHomePresenter extends EventPresenter<PieceReachedHome> {

    public PieceReachedHomePresenter() {
        super(PieceReachedHome.class);
    }

    @Override
    public String present(PieceReachedHome message) {
        return "  -> " + message.pieceLabel() + " reached Home and is removed from play!";
    }
}
