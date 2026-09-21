package model.player.rule.home;

import model.piece.Piece;
import model.rule.ChainedRule;

// Chain of Responsibility: each rule may forbid a HomeStraight overshoot.
public abstract class ExactRollRule extends ChainedRule<ExactRollRule> {

    public final boolean forbidsMove(Piece piece, int steps) {
        if (appliesTo(piece, steps)) {
            return true;
        }

        return getNextRule()
                .map(nextRule -> nextRule.forbidsMove(piece, steps))
                .orElse(false);
    }

    protected abstract boolean appliesTo(Piece piece, int steps);
}
