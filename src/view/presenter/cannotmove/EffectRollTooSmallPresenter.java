package view.presenter.cannotmove;

import message.cannotmove.EffectRollTooSmall;
import view.presenter.EventPresenter;

// Wording for a Sick effect cutting the roll to zero.
public final class EffectRollTooSmallPresenter extends EventPresenter<EffectRollTooSmall> {

    public EffectRollTooSmallPresenter() {
        super(EffectRollTooSmall.class);
    }

    @Override
    public String present(EffectRollTooSmall message) {
        return "  -> " + message.pieceLabel()
                + "\'s Sick effect halved this roll to zero cells and cannot move.";
    }
}
