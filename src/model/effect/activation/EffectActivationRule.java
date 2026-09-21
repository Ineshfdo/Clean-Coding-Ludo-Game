package model.effect.activation;

// T-15: Chain of Responsibility - an effect needs a genuine teleport.
public abstract class EffectActivationRule {

    private EffectActivationRule nextRule;

    public final void setNext(EffectActivationRule nextRule) {
        this.nextRule = nextRule;
    }

    public final boolean permitsActivation(MysteryCellArrival arrival) {
        if (!isSatisfiedBy(arrival)) {
            return false;
        }

        return nextRule == null || nextRule.permitsActivation(arrival);
    }

    protected abstract boolean isSatisfiedBy(MysteryCellArrival arrival);
}
