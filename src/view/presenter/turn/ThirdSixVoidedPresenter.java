package view.presenter.turn;

import message.turn.ThirdSixVoided;
import view.presenter.EventPresenter;

// Wording for a void third six.
public final class ThirdSixVoidedPresenter extends EventPresenter<ThirdSixVoided> {

    public ThirdSixVoidedPresenter() {
        super(ThirdSixVoided.class);
    }

    @Override
    public String present(ThirdSixVoided message) {
        return "  -> Three sixes in a row! This roll is void - turn passes to the next player.";
    }
}
