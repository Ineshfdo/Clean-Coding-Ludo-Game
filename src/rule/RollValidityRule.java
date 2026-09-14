package rule;

// Chain of Responsibility: each rule checks for a void
// condition, else defers onward.
public abstract class RollValidityRule {

    private RollValidityRule nextRule;

    public final RollValidityRule setNext(RollValidityRule nextRule) {
        this.nextRule = nextRule;
        return nextRule;
    }

    public final boolean isVoided(int rollNumber, int rollValue) {
        if (appliesTo(rollNumber, rollValue)) {
            return true;
        }
        return nextRule != null && nextRule.isVoided(rollNumber, rollValue);
    }

    protected abstract boolean appliesTo(int rollNumber, int rollValue);
}
