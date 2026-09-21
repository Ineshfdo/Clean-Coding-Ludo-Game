package view.presenter.game;

import message.game.RoundStarted;
import view.presenter.EventPresenter;

// Wording for a new round.
public final class RoundStartedPresenter extends EventPresenter<RoundStarted> {

    public RoundStartedPresenter() {
        super(RoundStarted.class);
    }

    @Override
    public String present(RoundStarted message) {
        return "\n\n" + message.roundNumber() + ". Round " + message.roundNumber();
    }
}
