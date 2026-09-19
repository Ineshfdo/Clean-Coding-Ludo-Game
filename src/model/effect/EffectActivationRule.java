package model.effect;

// T-15: Chain of Responsibility - validates that a piece reached Alpha/Beta/Gamma through
// genuine Mystery Cell teleportation before its associated effect is allowed to activate.
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
