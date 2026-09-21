package view;

import java.util.List;
import message.GameMessage;
import message.observer.GameMessageObserver;
import model.board.Board;
import model.player.Player;
import model.player.strategy.blockdirection.BlockTravelDirectionStrategy;
import view.presenter.MessagePresenter;

// Prints every game message as console text; the presenters decide the wording.
public final class ConsoleGameObserver implements GameMessageObserver {

    private final PresenterCatalog presenterCatalog;
    private final MessagePresenter messagePresenter;

    public ConsoleGameObserver(
            List<Player> allPlayers, Board board, BlockTravelDirectionStrategy blockTravelDirectionStrategy) {
        this.presenterCatalog = new PresenterCatalog(allPlayers, board, blockTravelDirectionStrategy);
        this.messagePresenter = new MessagePresenter(presenterCatalog.getPresenters());
    }

    public void setTurnOrder(List<Player> turnOrder) {
        presenterCatalog.setTurnOrder(turnOrder);
    }

    @Override
    public void onGameMessage(GameMessage message) {
        System.out.println(messagePresenter.present(message));
    }
}
