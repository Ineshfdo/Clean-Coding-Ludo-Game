package model.player.rule.home;

import model.piece.Piece;
import model.player.HomeEntryPolicy;
import model.rule.ChainedRule;

/**
 Chain of Responsibility that stops a piece from entering its HomeStraight too early (T-1, T-7).
 Every rule of the chain gets its say: one refusal is enough.
 */
public abstract class HomeStraightEntryRule extends ChainedRule<HomeStraightEntryRule>
        implements HomeEntryPolicy {

    /**
     Asks this rule and then the next rules of the chain.
     @param piece the piece that wants to enter its HomeStraight
     @return true when any rule of the chain refuses the entry
     */
    @Override
    public final boolean forbidsEntry(Piece piece) {
        if (appliesTo(piece)) {
            return true;
        }

        return getNextRule()
                .map(nextRule -> nextRule.forbidsEntry(piece))
                .orElse(false);
    }

    /**
     Checks the condition of this rule alone.
     @param piece the piece that wants to enter its HomeStraight
     @return true when this rule refuses the entry
     */
    protected abstract boolean appliesTo(Piece piece);
}
