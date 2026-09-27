package view;

import java.util.List;
import message.GameMessage;
import message.observer.GameMessageObserver;
import model.board.Board;
import model.player.Player;
import model.player.strategy.blockdirection.BlockTravelDirectionStrategy;
import view.presenter.MessagePresenter;

/**
 * Observer that prints every game message on the console. The presenters decide the wording.
 */
public final class ConsoleGameObserver implements GameMessageObserver {

    private final PresenterCatalog presenterCatalog;
    private final MessagePresenter messagePresenter;

    /**
     * Creates the console observer.
     *
     * @param allPlayers all players of the game, for the board report
     * @param board the board the pieces move on
     * @param blockTravelDirectionStrategy the strategy that decides the direction of a blockade
     */
    public ConsoleGameObserver(
            List<Player> allPlayers, Board board, BlockTravelDirectionStrategy blockTravelDirectionStrategy) {
        this.presenterCatalog = new PresenterCatalog(allPlayers, board, blockTravelDirectionStrategy);
        this.messagePresenter = new MessagePresenter(presenterCatalog.getPresenters());
    }

    /**
     * Sets the order in which the round reports list the players.
     *
     * @param turnOrder the players in play order, starting with the winner of the toss
     */
    public void setTurnOrder(List<Player> turnOrder) {
        presenterCatalog.setTurnOrder(turnOrder);
    }

    @Override
    public void onGameMessage(GameMessage message) {
        System.out.println(messagePresenter.present(message));
    }
}
