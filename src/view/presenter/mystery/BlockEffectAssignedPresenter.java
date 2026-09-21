package view.presenter.mystery;

import config.constant.EffectConstants;
import message.mystery.BlockEffectAssigned;
import view.presenter.EventPresenter;

// Wording for a block's shared Energized/Sick effect.
public final class BlockEffectAssignedPresenter extends EventPresenter<BlockEffectAssigned> {

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
