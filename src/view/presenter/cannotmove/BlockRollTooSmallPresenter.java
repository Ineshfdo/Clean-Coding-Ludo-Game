package view.presenter.cannotmove;

import message.cannotmove.BlockRollTooSmall;
import view.presenter.EventPresenter;

/**
 Presents {@link BlockRollTooSmall}: it says that the roll of a block was divided down to zero cells, so the block cannot move.
 */
public final class BlockRollTooSmallPresenter extends EventPresenter<BlockRollTooSmall> {

    /**
     Creates the presenter for {@link BlockRollTooSmall}.
     */
    public BlockRollTooSmallPresenter() {
        super(BlockRollTooSmall.class);
    }

    @Override
    public String present(BlockRollTooSmall message) {
        return "  -> " + message.pieceLabel() + "\'s block roll divided to zero cells and cannot move.";
    }
}
