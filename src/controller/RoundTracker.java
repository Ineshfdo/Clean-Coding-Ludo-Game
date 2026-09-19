package controller;

import java.util.List;
import model.player.Player;

public final class RoundTracker {

    private final List<Player> turnOrder;
    private int roundNumber;

    public RoundTracker(List<Player> turnOrder) {
        this.turnOrder = List.copyOf(turnOrder);
        this.roundNumber = 0;
    }

    public int startNextRound() {
        roundNumber++;
        return roundNumber;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public List<Player> getTurnOrder() {
        return turnOrder;
    }
}
