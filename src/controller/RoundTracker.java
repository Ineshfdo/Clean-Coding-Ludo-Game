package controller;

import java.util.List;
import model.player.Player;

/**
 * Counts the rounds of one game and keeps the turn order of the game.
 */
public final class RoundTracker {

    private final List<Player> turnOrder;
    private int roundNumber;

    /**
     * Creates a tracker that stands before the first round.
     *
     * @param turnOrder the players in play order; the list is copied
     */
    public RoundTracker(List<Player> turnOrder) {
        this.turnOrder = List.copyOf(turnOrder);
        this.roundNumber = 0;
    }

    /**
     * Starts the next round.
     *
     * @return the number of the round that has just started, counted from 1
     */
    public int startNextRound() {
        roundNumber++;
        return roundNumber;
    }

    /**
     * Gives the number of the latest round.
     *
     * @return the round number; 0 before the first round
     */
    public int getRoundNumber() {
        return roundNumber;
    }

    /**
     * Gives the turn order of the game.
     *
     * @return the players in play order; the list cannot be changed
     */
    public List<Player> getTurnOrder() {
        return turnOrder;
    }
}
