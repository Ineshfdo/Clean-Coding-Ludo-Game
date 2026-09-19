package model.player.rule;
import model.piece.Piece;

// Chain of Responsibility blocking early HomeStraight entry; lives in player to avoid a cycle.
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
