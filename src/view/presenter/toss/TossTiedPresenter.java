package view.presenter.toss;

import message.toss.TossTied;
import view.presenter.EventPresenter;

// Wording for a tie in the toss.
public final class TossTiedPresenter extends EventPresenter<TossTied> {

    public TossTiedPresenter() {
        super(TossTied.class);
    }

    @Override
    public String present(TossTied message) {
        return "There Was A Tie For The Highest Roll (" + message.rollValue()
                + ")! EVERYONE REROLLS...\n";
    }
}
