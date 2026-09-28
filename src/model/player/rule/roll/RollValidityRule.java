package model.player.rule.roll;

import model.rule.ChainedRule;

/**
 Chain of Responsibility that checks whether a roll is void.
 A third six in a row is void (rule 4).
 */
public abstract class RollValidityRule extends ChainedRule<RollValidityRule> {

    /**
     Asks this rule and then the next rules of the chain.
     @param consecutiveSixCount the number of sixes in a row, including this roll
     @param rollValue the value of the roll
     @return true when any rule of the chain voids the roll
     */
    public final boolean isVoided(int consecutiveSixCount, int rollValue) {
        if (appliesTo(consecutiveSixCount, rollValue)) {
            return true;
        }

        return getNextRule()
                .map(nextRule -> nextRule.isVoided(consecutiveSixCount, rollValue))
                .orElse(false);
    }

    /**
     Checks the condition of this rule alone.
     @param consecutiveSixCount the number of sixes in a row, including this roll
     @param rollValue the value of the roll
     @return true when this rule voids the roll
     */
    protected abstract boolean appliesTo(int consecutiveSixCount, int rollValue);
}
