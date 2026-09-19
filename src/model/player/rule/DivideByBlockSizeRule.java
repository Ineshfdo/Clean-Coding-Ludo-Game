package model.player.rule;

import java.util.List;
import model.piece.Piece;

// T-4: every block splits the roll evenly among its pieces.
public final class DivideByBlockSizeRule extends BlockMovementRule {

    @Override
    protected int restrict(List<Piece> blockPieces, int requestedSteps) {
        return requestedSteps / blockPieces.size();
    }
}
