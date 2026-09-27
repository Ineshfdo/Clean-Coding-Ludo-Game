package view.presenter.turn;

import message.turn.HomeGateOpened;
import utils.color.PlayerColorNames;
import view.presenter.EventPresenter;

/**
 * Presents {@link HomeGateOpened}: it tells that the home gate opened for a colour.
 */
public final class HomeGateOpenedPresenter extends EventPresenter<HomeGateOpened> {

    /**
     * Creates the presenter for {@link HomeGateOpened}.
     */
    public HomeGateOpenedPresenter() {
        super(HomeGateOpened.class);
    }

    @Override
    public String present(HomeGateOpened message) {
        return "The home gate opens for the " + PlayerColorNames.displayNameOf(message.playerColor())
                + " player: no opponent pieces remain to capture.";
    }
}
