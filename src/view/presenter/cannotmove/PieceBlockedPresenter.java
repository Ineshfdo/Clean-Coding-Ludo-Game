package view.presenter.cannotmove;

import message.cannotmove.PieceBlocked;
import view.presenter.EventPresenter;

// Wording for an opponent's blockade stopping a piece.
public final class PieceBlockedPresenter extends EventPresenter<PieceBlocked> {

    public PieceBlockedPresenter() {
        super(PieceBlocked.class);
    }

    @Override
    public String present(PieceBlocked message) {
        return "  -> " + message.pieceLabel()
                + " is blocked by an opponent\'s blockade and cannot move.";
    }
}
