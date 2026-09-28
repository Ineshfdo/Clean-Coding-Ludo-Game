package view.presenter.mystery;

import config.constant.EffectConstants;
import message.mystery.BetaRestrictionApplied;
import view.presenter.EventPresenter;

/**
 Presents {@link BetaRestrictionApplied}: it tells that a piece cannot move for four rounds because of the Beta restriction.
 */
public final class BetaRestrictionAppliedPresenter extends EventPresenter<BetaRestrictionApplied> {

    /**
     Creates the presenter for {@link BetaRestrictionApplied}.
     */
    public BetaRestrictionAppliedPresenter() {
        super(BetaRestrictionApplied.class);
    }

    @Override
    public String present(BetaRestrictionApplied message) {
        return "  -> " + message.pieceLabel() + " cannot move for the next "
                + EffectConstants.EFFECT_DURATION_IN_ROUNDS + " rounds (Beta restriction).";
    }
}
