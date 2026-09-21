package view.presenter.toss;

import message.toss.DiceRolled;
import view.presenter.EventPresenter;

// Wording for one toss roll.
public final class DiceRolledPresenter extends EventPresenter<DiceRolled> {

    public DiceRolledPresenter() {
        super(DiceRolled.class);
    }

    @Override
    public String present(DiceRolled message) {
        return message.playerColor() + " Player rolls a " + message.rollValue();
    }
}
