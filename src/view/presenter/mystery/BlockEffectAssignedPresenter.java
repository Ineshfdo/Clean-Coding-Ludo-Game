package view.presenter.mystery;

import config.constant.EffectConstants;
import message.mystery.BlockEffectAssigned;
import view.presenter.EventPresenter;

/**
 Presents {@link BlockEffectAssigned}: it tells the shared Energized or Sick effect of a blockade.
 */
public final class BlockEffectAssignedPresenter extends EventPresenter<BlockEffectAssigned> {

    /**
     Creates the presenter for {@link BlockEffectAssigned}.
     */
    public BlockEffectAssignedPresenter() {
        super(BlockEffectAssigned.class);
    }

    @Override
    public String present(BlockEffectAssigned message) {
        return "  -> Block " + message.blockLabel() + " is now " + message.effectLabel()
                + " (block effect, lasts " + EffectConstants.EFFECT_DURATION_IN_ROUNDS
                + " rounds, overrides individual effects).";
    }
}
