package model.effect.activation;

// T-15: allows an effect only after a genuine teleport arrival.
public final class MysteryTeleportActivationRule extends EffectActivationRule {

    @Override
    protected boolean allowsActivation(MysteryCellArrival arrival) {
        return arrival != null;
    }
}
