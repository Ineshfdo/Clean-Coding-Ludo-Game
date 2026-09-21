package view.presenter.mystery;

import config.constant.EffectConstants;
import message.mystery.BetaRestrictionApplied;
import view.presenter.EventPresenter;

// Wording for a Beta restriction starting.
public final class BetaRestrictionAppliedPresenter extends EventPresenter<BetaRestrictionApplied> {

    public BetaRestrictionAppliedPresenter() {
        super(BetaRestrictionApplied.class);
    }

    @Override
    public String present(BetaRestrictionApplied message) {
        return "  -> " + message.pieceLabel() + " cannot move for the next "
                + EffectConstants.EFFECT_DURATION_IN_ROUNDS + " rounds (Beta restriction).";
    }
}
