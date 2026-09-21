package model.player.rule.home;

import model.piece.Piece;
import model.player.HomeEntryPolicy;
import model.rule.ChainedRule;

// Chain of Responsibility: blocks early HomeStraight entry.
public abstract class HomeStraightEntryRule extends ChainedRule<HomeStraightEntryRule>
        implements HomeEntryPolicy {

    @Override
    public final boolean forbidsEntry(Piece piece) {
        if (appliesTo(piece)) {
            return true;
        }

        return getNextRule()
                .map(nextRule -> nextRule.forbidsEntry(piece))
                .orElse(false);
    }

    protected abstract boolean appliesTo(Piece piece);
}
