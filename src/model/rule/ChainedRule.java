package model.rule;

import java.util.Optional;

// Chain of Responsibility plumbing: each rule may hand over to the next one.
public abstract class ChainedRule<R extends ChainedRule<R>> {

    private R nextRule;

    public final void setNext(R nextRule) {
        this.nextRule = nextRule;
    }

    protected final Optional<R> getNextRule() {
        return Optional.ofNullable(nextRule);
    }
}
