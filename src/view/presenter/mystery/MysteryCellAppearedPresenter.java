package view.presenter.mystery;

import config.constant.MysteryCellConstants;
import message.mystery.MysteryCellAppeared;
import view.presenter.EventPresenter;

// Wording for the Mystery Cell appearing.
public final class MysteryCellAppearedPresenter extends EventPresenter<MysteryCellAppeared> {

    public MysteryCellAppearedPresenter() {
        super(MysteryCellAppeared.class);
    }

    @Override
    public String present(MysteryCellAppeared message) {
        return MysteryCellBanner.around(
                "A Mystery Cell has appeared at cell " + message.cellPosition()
                        + " will be here and will be at that location for the next "
                        + MysteryCellConstants.ROUNDS_PER_LOCATION + " rounds.");
    }
}
