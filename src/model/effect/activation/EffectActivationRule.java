package model.effect.activation;

// T-15: Chain of Responsibility - an effect needs a genuine teleport.
public abstract class EffectActivationRule {

    private EffectActivationRule nextRule;

    public final EffectActivationRule setNext(EffectActivationRule nextRule) {
        this.nextRule = nextRule;

        return nextRule;
    }

    public final boolean permitsActivation(MysteryCellArrival arrival) {
        if (!allowsActivation(arrival)) {
            return false;
        }

        return nextRule == null || nextRule.permitsActivation(arrival);
    }

    protected abstract boolean allowsActivation(MysteryCellArrival arrival);
}
