package view.presenter.cannotmove;

import message.cannotmove.EffectRollTooSmall;
import view.presenter.EventPresenter;

/**
 * Presents {@link EffectRollTooSmall}: it says that a Sick effect halved the roll down to zero
 * cells, so the piece cannot move.
 */
public final class EffectRollTooSmallPresenter extends EventPresenter<EffectRollTooSmall> {

    /**
     * Creates the presenter for {@link EffectRollTooSmall}.
     */
    public EffectRollTooSmallPresenter() {
        super(EffectRollTooSmall.class);
    }

    @Override
    public String present(EffectRollTooSmall message) {
        return "  -> " + message.pieceLabel()
                + "\'s Sick effect halved this roll to zero cells and cannot move.";
    }
}
