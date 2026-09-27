package model.player.rule.block;

import java.util.List;
import model.piece.Piece;
import model.rule.ChainedRule;

/**
 * Chain of Responsibility where each rule may change the number of steps of a blockade move, for
 * example by sharing the roll between its pieces (T-4).
 */
public abstract class BlockStepsRule extends ChainedRule<BlockStepsRule> {

    /**
     * Asks this rule and then the next rules of the chain.
     *
     * @param blockPieces the pieces of the blockade
     * @param requestedSteps the number of steps before the rule
     * @return the number of steps after all rules
     */
    public final int limitSteps(List<Piece> blockPieces, int requestedSteps) {
        int allowedSteps = restrict(blockPieces, requestedSteps);

        return getNextRule()
                .map(nextRule -> nextRule.limitSteps(blockPieces, allowedSteps))
                .orElse(allowedSteps);
    }

    /**
     * Changes the steps according to the condition of this rule alone.
     *
     * @param blockPieces the pieces of the blockade
     * @param requestedSteps the number of steps before the rule
     * @return the number of steps after this rule
     */
    protected abstract int restrict(List<Piece> blockPieces, int requestedSteps);
}
