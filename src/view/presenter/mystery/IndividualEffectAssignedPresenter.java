package view.presenter.mystery;

import config.constant.EffectConstants;
import message.mystery.IndividualEffectAssigned;
import view.presenter.EventPresenter;

/**
 Presents {@link IndividualEffectAssigned}: it tells the own Energized or Sick effect of a piece.
 */
public final class IndividualEffectAssignedPresenter extends EventPresenter<IndividualEffectAssigned> {

    /**
     Creates the presenter for {@link IndividualEffectAssigned}.
     */
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
