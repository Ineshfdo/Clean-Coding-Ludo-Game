package view;

import java.util.List;
import message.GameMessage;
import message.observer.GameMessageObserver;
import model.board.Board;
import model.player.Player;
import view.presenter.MessagePresenter;

/**
 Observer that prints every game message on the console.
 The presenters decide the wording.
 */
public final class ConsoleGameObserver implements GameMessageObserver {

    // Holds one presenter for every message type.
    private final PresenterCatalog presenterCatalog;
    // Finds the right presenter for each incoming message.
    private final MessagePresenter messagePresenter;

    /**
     Creates the console observer.
     @param allPlayers all players of the game, for the round report
     @param board the board the pieces move on
     */
    public ConsoleGameObserver(List<Player> allPlayers, Board board) {
        // Catalog builds the presenters; MessagePresenter uses them for lookup.
        this.presenterCatalog = new PresenterCatalog(allPlayers, board);
        this.messagePresenter = new MessagePresenter(presenterCatalog.getPresenters());
    }

    /**
     Sets the order in which the round reports list the players.
     @param turnOrder the players in play order, starting with the winner of the toss
     */
    public void setTurnOrder(List<Player> turnOrder) {
        // Passes the toss winner's play order on to the report.
        presenterCatalog.setTurnOrder(turnOrder);
    }

    @Override
    public void onGameMessage(GameMessage message) {
        // Observer pattern: called for every published message; prints it.
        System.out.println(messagePresenter.present(message));
    }
}
