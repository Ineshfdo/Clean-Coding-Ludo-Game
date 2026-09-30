package view.presenter.cannotmove;

import message.cannotmove.BlockRollTooSmall;
import view.presenter.EventPresenter;

/**
 Inherits from EventPresenter, which is set up ({@code <BlockRollTooSmall>}) to handle BlockRollTooSmall messages, so it reuses the parent's code.
 
 Presents {@link BlockRollTooSmall}: it says that the roll of a block was divided down to zero cells, so the block cannot move.
 */
public final class BlockRollTooSmallPresenter extends EventPresenter<BlockRollTooSmall> {

    /**
     Calls the parent EventPresenter constructor with BlockRollTooSmall.class, so the parent stores which message type this presenter handles.
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
