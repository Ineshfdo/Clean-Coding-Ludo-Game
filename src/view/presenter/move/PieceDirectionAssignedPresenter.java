package view.presenter.move;

import message.move.PieceDirectionAssigned;
import view.presenter.EventPresenter;

/**
 Presents {@link PieceDirectionAssigned}: it tells the direction that the coin toss gave to a piece.
 */
public final class PieceDirectionAssignedPresenter extends EventPresenter<PieceDirectionAssigned> {

    /**
     Creates the presenter for {@link PieceDirectionAssigned}.
     */
    public PieceDirectionAssignedPresenter() {
        super(PieceDirectionAssigned.class);
    }

    @Override
    public String present(PieceDirectionAssigned message) {
        return "  -> Coin toss for " + message.pieceLabel() + ": " + message.coinTossResultLabel()
                + " - it will move " + message.movementDirectionLabel() + ".";
    }
}
