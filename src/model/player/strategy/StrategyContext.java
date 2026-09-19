package model.player.strategy;

import java.util.List;
import model.board.Board;
import model.effect.mysterycell.MysteryCellLocation;
import model.player.Player;

// Read-only context a PlayerStrategy needs to evaluate legal options.
public final class StrategyContext {

    private final Player player;
    private final List<Player> allPlayers;

    private final Board board;
    private final MysteryCellLocation mysteryCellLocation;
    private final int rollNumber;

    public StrategyContext(
            Player player, List<Player> allPlayers, Board board,
            MysteryCellLocation mysteryCellLocation, int rollNumber) {
        this.player = player;
        this.allPlayers = allPlayers;
        this.board = board;
        this.mysteryCellLocation = mysteryCellLocation;
        this.rollNumber = rollNumber;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Player> getAllPlayers() {
        return allPlayers;
    }

    public Board getBoard() {
        return board;
    }

    // Lets Blue preview whether a move would land on the Mystery Cell.
    public MysteryCellLocation getMysteryCellLocation() {
        return mysteryCellLocation;
    }

    // Which roll this is in the current turn; tells a bonus roll from a new turn.
    public int getRollNumber() {
        return rollNumber;
    }
}
