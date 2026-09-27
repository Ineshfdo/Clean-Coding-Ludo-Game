package view.presenter.turn;

import message.turn.TurnStarted;
import view.presenter.EventPresenter;

/**
 * Presents {@link TurnStarted}: it announces the start of the turn of a player.
 */
public final class TurnStartedPresenter extends EventPresenter<TurnStarted> {

    /**
     * Creates the presenter for {@link TurnStarted}.
     */
    public TurnStartedPresenter() {
        super(TurnStarted.class);
    }

    @Override
    public String present(TurnStarted message) {
        return "\n- " + message.playerColor() + " Player\'s Turn -";
    }
}
