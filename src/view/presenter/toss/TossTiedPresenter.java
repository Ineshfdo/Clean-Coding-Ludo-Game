package view.presenter.toss;

import message.toss.TossTied;
import view.presenter.EventPresenter;

/**
 * Presents {@link TossTied}: it tells that the toss ended in a tie and everyone rolls again.
 */
public final class TossTiedPresenter extends EventPresenter<TossTied> {

    /**
     * Creates the presenter for {@link TossTied}.
     */
    public TossTiedPresenter() {
        super(TossTied.class);
    }

    @Override
    public String present(TossTied message) {
        return "There Was A Tie For The Highest Roll (" + message.rollValue()
                + ")! EVERYONE REROLLS...\n";
    }
}
