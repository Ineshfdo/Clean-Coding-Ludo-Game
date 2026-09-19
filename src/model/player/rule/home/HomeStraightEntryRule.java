package model.player.rule.home;

import model.piece.Piece;

// Chain of Responsibility: blocks early HomeStraight entry.
public abstract class HomeStraightEntryRule {

    private HomeStraightEntryRule nextRule;

    public final HomeStraightEntryRule setNext(HomeStraightEntryRule nextRule) {
        this.nextRule = nextRule;

        return nextRule;
    }

    public final boolean forbidsEntry(Piece piece) {
        if (appliesTo(piece)) {
            return true;
        }

        return nextRule != null && nextRule.forbidsEntry(piece);
    }

    protected abstract boolean appliesTo(Piece piece);
}
