package model.player.rule.roll;

import java.util.List;
import model.player.Player;

// What just happened: a roll that was accepted for this player.
public final class RollEvent {

    private final Player player;
    private final List<Player> allPlayers;
    private final int rollNumber;
    private final int rollValue;

    public RollEvent(Player player, List<Player> allPlayers, int rollNumber, int rollValue) {
        this.player = player;
        this.allPlayers = allPlayers;
        this.rollNumber = rollNumber;
        this.rollValue = rollValue;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Player> getAllPlayers() {
        return allPlayers;
    }

    public int getRollNumber() {
        return rollNumber;
    }

    public int getRollValue() {
        return rollValue;
    }
}
