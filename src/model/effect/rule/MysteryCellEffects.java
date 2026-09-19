package model.effect.rule;

import model.effect.activation.EffectActivationRule;

// T-12/T-14/T-15: bundles the Mystery Cell effect rules into one object.
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
