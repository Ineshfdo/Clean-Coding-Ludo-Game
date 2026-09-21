package model.player.rule.roll;

import model.rule.ChainedRule;

// Chain of Responsibility: each rule checks for a void condition.
public abstract class RollValidityRule extends ChainedRule<RollValidityRule> {

    public final boolean isVoided(int consecutiveSixCount, int rollValue) {
        if (appliesTo(consecutiveSixCount, rollValue)) {
            return true;
        }

        return getNextRule()
                .map(nextRule -> nextRule.isVoided(consecutiveSixCount, rollValue))
                .orElse(false);
    }

    protected abstract boolean appliesTo(int consecutiveSixCount, int rollValue);
}
