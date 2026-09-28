package view.presenter.game;

import message.game.RoundStarted;
import view.presenter.EventPresenter;

/**
 Presents {@link RoundStarted}: it announces the start of a new round.
 */
public final class RoundStartedPresenter extends EventPresenter<RoundStarted> {

    /**
     Creates the presenter for {@link RoundStarted}.
     */
    public RoundStartedPresenter() {
        super(RoundStarted.class);
    }

    @Override
    public String present(RoundStarted message) {
        return "\n\n" + message.roundNumber() + ". Round " + message.roundNumber();
    }
}
