package view.presenter.toss;

import message.toss.DiceRolled;
import view.presenter.EventPresenter;

/**
 Presents {@link DiceRolled}: it tells one roll of the first-player toss.
 */
public final class DiceRolledPresenter extends EventPresenter<DiceRolled> {

    /**
     Creates the presenter for {@link DiceRolled}.
     */
    public DiceRolledPresenter() {
        super(DiceRolled.class);
    }

    @Override
    public String present(DiceRolled message) {
        return message.playerColor() + " Player rolls a " + message.rollValue();
    }
}
