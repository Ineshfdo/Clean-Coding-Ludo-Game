package model.player.rule.block;

import java.util.List;
import model.piece.Piece;
import model.rule.ChainedRule;

// Chain of Responsibility: each rule may limit a block's step count.
public abstract class BlockStepsRule extends ChainedRule<BlockStepsRule> {

    public final int limitSteps(List<Piece> blockPieces, int requestedSteps) {
        int allowedSteps = restrict(blockPieces, requestedSteps);

        return getNextRule()
                .map(nextRule -> nextRule.limitSteps(blockPieces, allowedSteps))
                .orElse(allowedSteps);
    }

    protected abstract int restrict(List<Piece> blockPieces, int requestedSteps);
}
