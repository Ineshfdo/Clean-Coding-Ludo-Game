package view.presenter.turn;

import message.turn.TurnStarted;
import view.presenter.EventPresenter;

// Wording for the start of a turn.
public final class TurnStartedPresenter extends EventPresenter<TurnStarted> {

    public TurnStartedPresenter() {
        super(TurnStarted.class);
    }

    @Override
    public String present(TurnStarted message) {
        return "\n- " + message.playerColor() + " Player\'s Turn -";
    }
}
