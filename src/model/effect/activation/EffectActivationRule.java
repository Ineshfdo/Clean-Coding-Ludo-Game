package model.effect.activation;

import model.rule.ChainedRule;

// T-15: Chain of Responsibility - an effect needs a genuine teleport.
public abstract class EffectActivationRule extends ChainedRule<EffectActivationRule> {

    public final boolean permitsActivation(MysteryCellArrival arrival) {
        if (!isSatisfiedBy(arrival)) {
            return false;
        }

        return getNextRule()
                .map(nextRule -> nextRule.permitsActivation(arrival))
                .orElse(true);
    }

    protected abstract boolean isSatisfiedBy(MysteryCellArrival arrival);
}
