package model.player.rule.block;

import java.util.List;
import model.piece.Piece;

// T-4: a block splits the roll evenly among its pieces.
public final class DivideByBlockSizeRule extends BlockStepsRule {

    @Override
    protected int restrict(List<Piece> blockPieces, int requestedSteps) {
        return requestedSteps / blockPieces.size();
    }
}
