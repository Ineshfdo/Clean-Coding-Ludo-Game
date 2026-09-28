package view.presenter.turn;

import message.turn.NoPieceMovable;
import view.presenter.EventPresenter;

/**
 Presents {@link NoPieceMovable}: it tells that the roll left nothing to move.
 */
public final class NoPieceMovablePresenter extends EventPresenter<NoPieceMovable> {

    /**
     Creates the presenter for {@link NoPieceMovable}.
     */
    public NoPieceMovablePresenter() {
        super(NoPieceMovable.class);
    }

    @Override
    public String present(NoPieceMovable message) {
        return "  -> No pieces on the board could be moved.";
    }
}
