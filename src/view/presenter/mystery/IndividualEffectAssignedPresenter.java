package view.presenter.mystery;

import config.constant.EffectConstants;
import message.mystery.IndividualEffectAssigned;
import view.presenter.EventPresenter;

// Wording for a piece's own Energized/Sick effect.
public final class IndividualEffectAssignedPresenter extends EventPresenter<IndividualEffectAssigned> {

    public IndividualEffectAssignedPresenter() {
        super(IndividualEffectAssigned.class);
    }

    @Override
    public String present(IndividualEffectAssigned message) {
        return "  -> " + message.pieceLabel() + " is now " + message.effectLabel()
                + " (individual effect, lasts " + EffectConstants.EFFECT_DURATION_IN_ROUNDS
                + " rounds).";
    }
}
