package view.presenter.move;

import message.move.BlockMoved;
import view.presenter.EventPresenter;

/**
 * Presents {@link BlockMoved}: it tells the move of a blockade: its cells, its type and its
 * direction.
 */
public final class BlockMovedPresenter extends EventPresenter<BlockMoved> {

    /**
     * Creates the presenter for {@link BlockMoved}.
     */
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
