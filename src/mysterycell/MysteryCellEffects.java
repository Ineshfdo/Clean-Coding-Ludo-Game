package mysterycell;

// T-12/T-14/T-15: bundles TeleportCommand's destination-specific collaborators into one
// object, so adding a future Mystery Cell effect does not keep growing TeleportCommand's
// (and MysteryCellTeleportRule's) constructor parameter list.
public final class MysteryCellEffects {

    private final AlphaEffectRule alphaEffectRule;
    private final GammaDirectionRule gammaDirectionRule;
    private final EffectActivationRule effectActivationRule;

    public MysteryCellEffects(
            AlphaEffectRule alphaEffectRule, GammaDirectionRule gammaDirectionRule,
            EffectActivationRule effectActivationRule) {
        this.alphaEffectRule = alphaEffectRule;
        this.gammaDirectionRule = gammaDirectionRule;
        this.effectActivationRule = effectActivationRule;
    }

    public AlphaEffectRule getAlphaEffectRule() {
        return alphaEffectRule;
    }

    public GammaDirectionRule getGammaDirectionRule() {
        return gammaDirectionRule;
    }

    public EffectActivationRule getEffectActivationRule() {
        return effectActivationRule;
    }
}
