package view.presenter.mystery;

import config.constant.MysteryCellConstants;
import message.mystery.MysteryCellRelocated;
import view.presenter.EventPresenter;

// Wording for the Mystery Cell moving.
public final class MysteryCellRelocatedPresenter extends EventPresenter<MysteryCellRelocated> {

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
