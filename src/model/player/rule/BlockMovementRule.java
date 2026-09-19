package model.player.rule;

import java.util.List;

import model.piece.Piece;

// Chain of Responsibility: each rule may further restrict a block's legal step count.
public abstract class BlockMovementRule {

    private BlockMovementRule nextRule;

    public final BlockMovementRule setNext(BlockMovementRule nextRule) {
        this.nextRule = nextRule;
        return nextRule;
    }

    public final int limitSteps(List<Piece> blockPieces, int requestedSteps) {
        int allowedSteps = restrict(blockPieces, requestedSteps);
        return nextRule == null ? allowedSteps : nextRule.limitSteps(blockPieces, allowedSteps);
    }

    protected abstract int restrict(List<Piece> blockPieces, int requestedSteps);
}
