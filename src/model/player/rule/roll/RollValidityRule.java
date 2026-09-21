package model.player.rule.roll;

// Chain of Responsibility: each rule checks for a void condition.
public abstract class RollValidityRule {

    private RollValidityRule nextRule;

    public final void setNext(RollValidityRule nextRule) {
        this.nextRule = nextRule;
    }

    public final boolean isVoided(int consecutiveSixCount, int rollValue) {
        if (appliesTo(consecutiveSixCount, rollValue)) {
            return true;
        }

        return nextRule != null && nextRule.isVoided(consecutiveSixCount, rollValue);
    }

    protected abstract boolean appliesTo(int consecutiveSixCount, int rollValue);
}
