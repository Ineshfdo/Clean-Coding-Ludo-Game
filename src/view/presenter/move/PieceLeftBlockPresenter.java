package view.presenter.move;

import message.move.PieceLeftBlock;
import view.presenter.EventPresenter;

/**
 Presents {@link PieceLeftBlock}: it tells that a piece left its blockade and resumes its own direction.
 */
public final class PieceLeftBlockPresenter extends EventPresenter<PieceLeftBlock> {

    /**
     Creates the presenter for {@link PieceLeftBlock}.
     */
    public PieceLeftBlockPresenter() {
        super(PieceLeftBlock.class);
    }

    @Override
    public String present(PieceLeftBlock message) {
        return "  -> " + message.pieceLabel() + " leaves the block and resumes its own direction.";
    }
}
