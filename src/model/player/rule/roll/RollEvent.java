package model.player.rule.roll;

import java.util.List;
import model.player.Player;

/**
 A roll that was accepted for a player.
 It is given to the roll hooks.
 */
public final class RollEvent {

    private final Player player;
    private final List<Player> allPlayers;
    private final int rollNumber;
    private final int rollValue;

    /**
     Creates the event.
     @param player the player who rolled
     @param allPlayers all players of the game
     @param rollNumber the place of the roll in the turn; 1 is the first roll
     @param rollValue the value of the roll
     */
    public RollEvent(Player player, List<Player> allPlayers, int rollNumber, int rollValue) {
        this.player = player;
        this.allPlayers = allPlayers;
        this.rollNumber = rollNumber;
        this.rollValue = rollValue;
    }

    /**
     Gives the player who rolled.
     @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     Gives all players of the game.
     @return all players
     */
    public List<Player> getAllPlayers() {
        return allPlayers;
    }

    /**
     Gives the place of the roll in the turn.
     @return 1 for the first roll of a turn, and higher for bonus rolls
     */
    public int getRollNumber() {
        return rollNumber;
    }

    /**
     Gives the value of the roll.
     @return the rolled value
     */
    public int getRollValue() {
        return rollValue;
    }
}
