package view.presenter.toss;

import message.toss.TossStarting;
import view.presenter.EventPresenter;

// Wording for the start of the first-player toss.
public final class TossStartingPresenter extends EventPresenter<TossStarting> {

    public TossStartingPresenter() {
        super(TossStarting.class);
    }

    @Override
    public String present(TossStarting message) {
        return "Rolling The Dice To Determine Who Goes First\n--------------------------------------------\n";
    }
}
