package rule;

import java.util.List;
import player.Piece;

// T-4: every block splits the roll evenly among its pieces.
public final class DivideByBlockSizeRule extends BlockMovementRule {

    @Override
    protected int restrict(List<Piece> blockPieces, int requestedSteps) {
        return requestedSteps / blockPieces.size();
    }
}
