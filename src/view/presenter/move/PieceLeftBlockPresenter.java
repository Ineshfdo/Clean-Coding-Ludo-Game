package view.presenter.move;

import message.move.PieceLeftBlock;
import view.presenter.EventPresenter;

// Wording for a piece leaving its block.
public final class PieceLeftBlockPresenter extends EventPresenter<PieceLeftBlock> {

    public PieceLeftBlockPresenter() {
        super(PieceLeftBlock.class);
    }

    @Override
    public String present(PieceLeftBlock message) {
        return "  -> " + message.pieceLabel() + " leaves the block and resumes its own direction.";
    }
}
