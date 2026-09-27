package view.presenter.toss;

import message.toss.TossWon;
import view.presenter.EventPresenter;

/**
 * Presents {@link TossWon}: it tells who won the toss.
 */
public final class TossWonPresenter extends EventPresenter<TossWon> {

    /**
     * Creates the presenter for {@link TossWon}.
     */
    public TossWonPresenter() {
        super(TossWon.class);
    }

    @Override
    public String present(TossWon message) {
        return message.playerColor() + " Player Won The Toss With A " + message.rollValue()
                + " And Goes First!";
    }
}
