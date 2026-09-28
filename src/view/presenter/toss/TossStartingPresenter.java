package view.presenter.toss;

import message.toss.TossStarting;
import view.presenter.EventPresenter;

/**
 Presents {@link TossStarting}: it announces the start of the first-player toss.
 */
public final class TossStartingPresenter extends EventPresenter<TossStarting> {

    /**
     Creates the presenter for {@link TossStarting}.
     */
    public TossStartingPresenter() {
        super(TossStarting.class);
    }

    @Override
    public String present(TossStarting message) {
        return "Rolling The Dice To Determine Who Goes First\n--------------------------------------------\n";
    }
}
