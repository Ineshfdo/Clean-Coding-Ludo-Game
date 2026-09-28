package view.presenter.move;

import message.move.PieceReachedHome;
import view.presenter.EventPresenter;

/**
 Presents {@link PieceReachedHome}: it tells that a piece or a blockade reached Home and is removed from play.
 */
public final class PieceReachedHomePresenter extends EventPresenter<PieceReachedHome> {

    /**
     Creates the presenter for {@link PieceReachedHome}.
     */
    public PieceReachedHomePresenter() {
        super(PieceReachedHome.class);
    }

    @Override
    public String present(PieceReachedHome message) {
        return "  -> " + message.pieceLabel() + " reached Home and is removed from play!";
    }
}
