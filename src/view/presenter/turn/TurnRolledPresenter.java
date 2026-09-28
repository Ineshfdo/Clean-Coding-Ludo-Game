package view.presenter.turn;

import message.turn.TurnRolled;
import utils.color.PlayerColorNames;
import view.presenter.EventPresenter;

/**
 Presents {@link TurnRolled}: it tells the roll of a player during a turn.
 */
public final class TurnRolledPresenter extends EventPresenter<TurnRolled> {

    /**
     Creates the presenter for {@link TurnRolled}.
     */
    public TurnRolledPresenter() {
        super(TurnRolled.class);
    }

    @Override
    public String present(TurnRolled message) {
        return PlayerColorNames.displayNameOf(message.playerColor()) + " player rolled "
                + message.rollValue();
    }
}
