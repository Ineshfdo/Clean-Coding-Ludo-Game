package rule;

import java.util.List;

import player.BlockDirectionType;
import player.Piece;

// T-4: an opposite-direction block splits the roll evenly; a same-direction block is unaffected.
public final class MixedDirectionBlockMovementRule extends BlockMovementRule {

    @Override
    protected int restrict(List<Piece> blockPieces, int requestedSteps) {
        if (BlockDirectionType.classify(blockPieces) != BlockDirectionType.OPPOSITE_DIRECTION) {
            return requestedSteps;
        }
        return requestedSteps / blockPieces.size();
    }
}
