package model.player.rule.home;

import model.piece.Piece;

// Chain of Responsibility: each rule may forbid a HomeStraight overshoot.
public abstract class ExactRollRule {

    private ExactRollRule nextRule;

    public final void setNext(ExactRollRule nextRule) {
        this.nextRule = nextRule;
    }

    public final boolean forbidsMove(Piece piece, int steps) {
        if (appliesTo(piece, steps)) {
            return true;
        }

        return nextRule != null && nextRule.forbidsMove(piece, steps);
    }

    protected abstract boolean appliesTo(Piece piece, int steps);
}
