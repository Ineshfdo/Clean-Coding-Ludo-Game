package model.player.strategy;

import java.util.List;
import model.board.Board;
import model.effect.mysterycell.MysteryCellLocation;
import model.player.Player;

/**
 The situation that a strategy needs to judge the legal commands: the player, all players, the board, the Mystery Cell and the number of the roll.
 */
public final class StrategyContext {

    private final Player player;
    private final List<Player> allPlayers;

    private final Board board;
    private final MysteryCellLocation mysteryCellLocation;
    private final int rollNumber;

    /**
     Creates the context of one roll.
     @param player the player who chooses
     @param allPlayers all players of the game
     @param board the board the pieces move on
     @param mysteryCellLocation read-only view of the Mystery Cell
     @param rollNumber the place of the roll in the turn; 1 is the first roll
     */
    public StrategyContext(
            Player player, List<Player> allPlayers, Board board,
            MysteryCellLocation mysteryCellLocation, int rollNumber) {
        this.player = player;
        this.allPlayers = allPlayers;
        this.board = board;
        this.mysteryCellLocation = mysteryCellLocation;
        this.rollNumber = rollNumber;
    }

    /**
     Gives the player who chooses.
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
     Gives the board.
     @return the board the pieces move on
     */
    public Board getBoard() {
        return board;
    }

    /**
     Gives the Mystery Cell, so that Blue can preview whether a move lands on it.
     @return the read-only view of the Mystery Cell
     */
    public MysteryCellLocation getMysteryCellLocation() {
        return mysteryCellLocation;
    }

    /**
     Tells which roll of the turn this is, so a bonus roll can be told from a new turn.
     @return 1 for the first roll of a turn, and higher for bonus rolls
     */
    public int getRollNumber() {
        return rollNumber;
    }
}
