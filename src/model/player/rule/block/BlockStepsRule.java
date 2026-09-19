package model.player.rule.block;

import java.util.List;
import model.piece.Piece;

// Chain of Responsibility: each rule may limit a block's step count.
public abstract class BlockStepsRule {

    private BlockStepsRule nextRule;

    public final BlockStepsRule setNext(BlockStepsRule nextRule) {
        this.nextRule = nextRule;

        return nextRule;
    }

    public final int limitSteps(List<Piece> blockPieces, int requestedSteps) {
        int allowedSteps = restrict(blockPieces, requestedSteps);

        return nextRule == null ? allowedSteps : nextRule.limitSteps(blockPieces, allowedSteps);
    }

    protected abstract int restrict(List<Piece> blockPieces, int requestedSteps);
}
