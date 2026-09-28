package view.presenter.game;

import message.game.GameStarting;
import view.presenter.EventPresenter;

/**
 Presents {@link GameStarting}: it announces the start of the game.
 */
public final class GameStartingPresenter extends EventPresenter<GameStarting> {

    /**
     Creates the presenter for {@link GameStarting}.
     */
    public GameStartingPresenter() {
        super(GameStarting.class);
    }

    @Override
    public String present(GameStarting message) {
        return "\nStarting the Ludo game!\n";
    }
}
