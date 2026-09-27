package model.rule;

import java.util.Optional;

/**
 * Base class of the Chain of Responsibility. Each rule may hand the request over to the next rule.
 * The type parameter is the kind of rule in the chain, so only rules of the same kind can be
 * linked.
 *
 * @param <R> the kind of rule in the chain
 */
public abstract class ChainedRule<R extends ChainedRule<R>> {

    private R nextRule;

    /**
     * Links the rule that is asked after this one.
     *
     * @param nextRule the next rule of the chain
     */
    public final void setNext(R nextRule) {
        this.nextRule = nextRule;
    }

    /**
     * Gives the next rule.
     *
     * @return the next rule, or an empty result at the end of the chain
     */
    protected final Optional<R> getNextRule() {
        return Optional.ofNullable(nextRule);
    }
}
