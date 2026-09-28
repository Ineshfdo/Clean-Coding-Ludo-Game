package view.presenter.mystery;

import message.mystery.PieceDirectionReversed;
import view.presenter.EventPresenter;

/**
 Presents {@link PieceDirectionReversed}: it tells that a Gamma teleport reversed a direction.
 */
public final class PieceDirectionReversedPresenter extends EventPresenter<PieceDirectionReversed> {

    /**
     Creates the presenter for {@link PieceDirectionReversed}.
     */
    public PieceDirectionReversedPresenter() {
        super(PieceDirectionReversed.class);
    }

    @Override
    public String present(PieceDirectionReversed message) {
        return "  -> " + message.pieceLabel() + " landed on Gamma and reversed direction - now moving "
                + message.newDirectionLabel() + ".";
    }
}
