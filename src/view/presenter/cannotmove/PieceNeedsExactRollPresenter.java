package view.presenter.cannotmove;

import message.cannotmove.PieceNeedsExactRoll;
import view.presenter.EventPresenter;

/**
 Presents {@link PieceNeedsExactRoll}: it says that the piece needs an exact roll to reach Home.
 */
public final class PieceNeedsExactRollPresenter extends EventPresenter<PieceNeedsExactRoll> {

    /**
     Creates the presenter for {@link PieceNeedsExactRoll}.
     */
    public PieceNeedsExactRollPresenter() {
        super(PieceNeedsExactRoll.class);
    }

    @Override
    public String present(PieceNeedsExactRoll message) {
        return "  -> " + message.pieceLabel() + " needs an exact roll to reach Home and cannot move.";
    }
}
