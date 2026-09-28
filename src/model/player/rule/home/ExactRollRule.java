package model.player.rule.home;

import model.piece.Piece;
import model.rule.ChainedRule;

/**
 Chain of Responsibility where each rule may forbid a move on the HomeStraight that would go past Home (rule 10).
 */
public abstract class ExactRollRule extends ChainedRule<ExactRollRule> {

    /**
     Asks this rule and then the next rules of the chain.
     @param piece the piece on its HomeStraight
     @param steps the number of steps of the move
     @return true when any rule of the chain forbids the move
     */
    public final boolean forbidsMove(Piece piece, int steps) {
        if (appliesTo(piece, steps)) {
            return true;
        }

        return getNextRule()
                .map(nextRule -> nextRule.forbidsMove(piece, steps))
                .orElse(false);
    }

    /**
     Checks the condition of this rule alone.
     @param piece the piece on its HomeStraight
     @param steps the number of steps of the move
     @return true when this rule forbids the move
     */
    protected abstract boolean appliesTo(Piece piece, int steps);
}
