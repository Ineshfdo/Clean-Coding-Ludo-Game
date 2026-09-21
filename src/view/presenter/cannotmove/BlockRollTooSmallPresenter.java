package view.presenter.cannotmove;

import message.cannotmove.BlockRollTooSmall;
import view.presenter.EventPresenter;

// Wording for a block's roll dropping to zero.
public final class BlockRollTooSmallPresenter extends EventPresenter<BlockRollTooSmall> {

    public BlockRollTooSmallPresenter() {
        super(BlockRollTooSmall.class);
    }

    @Override
    public String present(BlockRollTooSmall message) {
        return "  -> " + message.pieceLabel() + "\'s block roll divided to zero cells and cannot move.";
    }
}
