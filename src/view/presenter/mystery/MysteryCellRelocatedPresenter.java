package view.presenter.mystery;

import config.constant.MysteryCellConstants;
import message.mystery.MysteryCellRelocated;
import view.presenter.EventPresenter;

/**
 Presents {@link MysteryCellRelocated}: it announces that the Mystery Cell moved to a new cell, in a banner.
 */
public final class MysteryCellRelocatedPresenter extends EventPresenter<MysteryCellRelocated> {

    /**
     Creates the presenter for {@link MysteryCellRelocated}.
     */
    public MysteryCellRelocatedPresenter() {
        super(MysteryCellRelocated.class);
    }

    @Override
    public String present(MysteryCellRelocated message) {
        return MysteryCellBanner.around(
                "The Mystery Cell has relocated to cell " + message.cellPosition()
                        + " will be here and will be at that location for the next "
                        + MysteryCellConstants.ROUNDS_PER_LOCATION + " rounds.");
    }
}
