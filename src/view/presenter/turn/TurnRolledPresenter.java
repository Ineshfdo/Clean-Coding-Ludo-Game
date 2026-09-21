package view.presenter.turn;

import message.turn.TurnRolled;
import utils.color.PlayerColorNames;
import view.presenter.EventPresenter;

// Wording for a roll during a turn.
public final class TurnRolledPresenter extends EventPresenter<TurnRolled> {

    public TurnRolledPresenter() {
        super(TurnRolled.class);
    }

    @Override
    public String present(TurnRolled message) {
        return PlayerColorNames.displayNameOf(message.playerColor()) + " player rolled "
                + message.rollValue();
    }
}
