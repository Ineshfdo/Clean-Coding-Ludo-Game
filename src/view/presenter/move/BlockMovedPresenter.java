package view.presenter.move;

import message.move.BlockMoved;
import view.presenter.EventPresenter;

// T-4/T-13: a block move names its BlockType and BlockDirection.
public final class BlockMovedPresenter extends EventPresenter<BlockMoved> {

    public BlockMovedPresenter() {
        super(BlockMoved.class);
    }

    @Override
    public String present(BlockMoved message) {
        return "  -> Moved " + message.blockLabel() + " from cell " + message.fromPosition()
                + " to cell " + message.newPosition() + "." + " [BlockType:" + message.blockTypeLabel()
                + " BlockDirection:" + message.movementDirectionLabel() + "]";
    }
}
