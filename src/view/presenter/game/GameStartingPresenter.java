package view.presenter.game;

import message.game.GameStarting;
import view.presenter.EventPresenter;

// Wording for the start of the game.
public final class GameStartingPresenter extends EventPresenter<GameStarting> {

    public GameStartingPresenter() {
        super(GameStarting.class);
    }

    @Override
    public String present(GameStarting message) {
        return "\nStarting the Ludo game!\n";
    }
}
