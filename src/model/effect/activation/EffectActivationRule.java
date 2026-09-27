package model.effect.activation;

import model.rule.ChainedRule;

/**
 * Chain of Responsibility that decides whether an effect may start after a Mystery Cell arrival
 * (T-15). An effect needs a genuine teleport.
 */
public abstract class EffectActivationRule extends ChainedRule<EffectActivationRule> {

    /**
     * Asks this rule and then the next rules of the chain.
     *
     * @param arrival the arrival to check
     * @return true only when every rule of the chain is satisfied
     */
    public final boolean permitsActivation(MysteryCellArrival arrival) {
        if (!isSatisfiedBy(arrival)) {
            return false;
        }

        return getNextRule()
                .map(nextRule -> nextRule.permitsActivation(arrival))
                .orElse(true);
    }

    /**
     * Checks the condition of this rule alone.
     *
     * @param arrival the arrival to check
     * @return true when this rule allows the activation
     */
    protected abstract boolean isSatisfiedBy(MysteryCellArrival arrival);
}
