package rule;

// Chain of Responsibility: each rule decides whether it recognizes
// a void condition for this roll; if not, it defers to the next
// rule in the chain. isVoided() is the fixed chain-walking
// template; appliesTo() is the one thing each concrete rule
// implements.
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
