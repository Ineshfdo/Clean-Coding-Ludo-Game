package view.presenter.move;

import message.move.PieceDirectionAssigned;
import view.presenter.EventPresenter;

// Wording for the direction a coin toss gave a piece.
public final class PieceDirectionAssignedPresenter extends EventPresenter<PieceDirectionAssigned> {

    public PieceDirectionAssignedPresenter() {
        super(PieceDirectionAssigned.class);
    }

    @Override
    public String present(PieceDirectionAssigned message) {
        return "  -> Coin toss for " + message.pieceLabel() + ": " + message.coinTossResultLabel()
                + " - it will move " + message.movementDirectionLabel() + ".";
    }
}
