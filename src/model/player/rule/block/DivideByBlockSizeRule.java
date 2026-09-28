package model.player.rule.block;

import java.util.List;
import model.piece.Piece;

/**
 Shares the roll of a blockade evenly between its pieces, rounded down (T-4).
 */
public final class DivideByBlockSizeRule extends BlockStepsRule {

    @Override
    protected int restrict(List<Piece> blockPieces, int requestedSteps) {
        return requestedSteps / blockPieces.size();
    }
}
