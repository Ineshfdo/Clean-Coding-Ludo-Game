package view.presenter.turn;

import message.turn.NoPieceMovable;
import view.presenter.EventPresenter;

// Wording for a roll that leaves nothing to move.
public final class NoPieceMovablePresenter extends EventPresenter<NoPieceMovable> {

    public NoPieceMovablePresenter() {
        super(NoPieceMovable.class);
    }

    @Override
    public String present(NoPieceMovable message) {
        return "  -> No pieces on the board could be moved.";
    }
}
