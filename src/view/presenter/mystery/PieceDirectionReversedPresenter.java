package view.presenter.mystery;

import message.mystery.PieceDirectionReversed;
import view.presenter.EventPresenter;

// Wording for a Gamma teleport reversing direction.
public final class PieceDirectionReversedPresenter extends EventPresenter<PieceDirectionReversed> {

    public PieceDirectionReversedPresenter() {
        super(PieceDirectionReversed.class);
    }

    @Override
    public String present(PieceDirectionReversed message) {
        return "  -> " + message.pieceLabel() + " landed on Gamma and reversed direction - now moving "
                + message.newDirectionLabel() + ".";
    }
}
