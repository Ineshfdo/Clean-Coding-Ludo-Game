package view.presenter.cannotmove;

import message.cannotmove.PieceBlocked;
import view.presenter.EventPresenter;

/**
 * Presents {@link PieceBlocked}: it says that an opponent's blockade stops the piece or the block.
 */
public final class PieceBlockedPresenter extends EventPresenter<PieceBlocked> {

    /**
     * Creates the presenter for {@link PieceBlocked}.
     */
    public PieceBlockedPresenter() {
        super(PieceBlocked.class);
    }

    @Override
    public String present(PieceBlocked message) {
        return "  -> " + message.pieceLabel()
                + " is blocked by an opponent\'s blockade and cannot move.";
    }
}
