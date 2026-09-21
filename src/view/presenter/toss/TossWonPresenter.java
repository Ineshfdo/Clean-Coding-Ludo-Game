package view.presenter.toss;

import message.toss.TossWon;
import view.presenter.EventPresenter;

// Wording for the toss winner.
public final class TossWonPresenter extends EventPresenter<TossWon> {

    public TossWonPresenter() {
        super(TossWon.class);
    }

    @Override
    public String present(TossWon message) {
        return message.playerColor() + " Player Won The Toss With A " + message.rollValue()
                + " And Goes First!";
    }
}
