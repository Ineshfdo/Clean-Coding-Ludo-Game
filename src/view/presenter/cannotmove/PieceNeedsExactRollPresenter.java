package view.presenter.cannotmove;

import message.cannotmove.PieceNeedsExactRoll;
import view.presenter.EventPresenter;

// Wording for needing an exact roll.
public final class PieceNeedsExactRollPresenter extends EventPresenter<PieceNeedsExactRoll> {

    public PieceNeedsExactRollPresenter() {
        super(PieceNeedsExactRoll.class);
    }

    @Override
    public String present(PieceNeedsExactRoll message) {
        return "  -> " + message.pieceLabel() + " needs an exact roll to reach Home and cannot move.";
    }
}
