package model.effect.activation;

/**
 * Allows an effect only after a genuine teleport arrival (T-15).
 */
public final class MysteryTeleportActivationRule extends EffectActivationRule {

    @Override
    protected boolean isSatisfiedBy(MysteryCellArrival arrival) {
        return arrival != null;
    }
}
