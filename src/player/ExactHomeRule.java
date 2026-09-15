package player;

// Chain of Responsibility: each rule may forbid a HomeStraight overshoot, else defers.
public abstract class ExactHomeRule {

    private ExactHomeRule nextRule;

    public final ExactHomeRule setNext(ExactHomeRule nextRule) {
        this.nextRule = nextRule;
        return nextRule;
    }

    public final boolean forbidsMove(Piece piece, int steps) {
        if (appliesTo(piece, steps)) {
            return true;
        }
        return nextRule != null && nextRule.forbidsMove(piece, steps);
    }

    protected abstract boolean appliesTo(Piece piece, int steps);
}
