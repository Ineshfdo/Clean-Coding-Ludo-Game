package model.player.rule;

// Chain of Responsibility: each rule checks for a void
// condition, else defers onward.
public abstract class RollValidityRule {

    private RollValidityRule nextRule;

    public final RollValidityRule setNext(RollValidityRule nextRule) {
        this.nextRule = nextRule;
        return nextRule;
    }

    public final boolean isVoided(int consecutiveSixCount, int rollValue) {
        if (appliesTo(consecutiveSixCount, rollValue)) {
            return true;
        }
        return nextRule != null && nextRule.isVoided(consecutiveSixCount, rollValue);
    }

    protected abstract boolean appliesTo(int consecutiveSixCount, int rollValue);
}
