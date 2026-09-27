package view.presenter.turn;

import message.turn.ThirdSixVoided;
import view.presenter.EventPresenter;

/**
 * Presents {@link ThirdSixVoided}: it tells that a third six in a row is void and the turn passes.
 */
public final class ThirdSixVoidedPresenter extends EventPresenter<ThirdSixVoided> {

    /**
     * Creates the presenter for {@link ThirdSixVoided}.
     */
    public ThirdSixVoidedPresenter() {
        super(ThirdSixVoided.class);
    }

    @Override
    public String present(ThirdSixVoided message) {
        return "  -> Three sixes in a row! This roll is void - turn passes to the next player.";
    }
}
