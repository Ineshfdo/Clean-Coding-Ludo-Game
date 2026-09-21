package view.presenter.turn;

import message.turn.HomeGateOpened;
import utils.color.PlayerColorNames;
import view.presenter.EventPresenter;

// Wording for the home gate opening.
public final class HomeGateOpenedPresenter extends EventPresenter<HomeGateOpened> {

    public HomeGateOpenedPresenter() {
        super(HomeGateOpened.class);
    }

    @Override
    public String present(HomeGateOpened message) {
        return "The home gate opens for the " + PlayerColorNames.displayNameOf(message.playerColor())
                + " player: no opponent pieces remain to capture.";
    }
}
