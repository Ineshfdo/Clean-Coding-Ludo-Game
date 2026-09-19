package model.player.strategy;

import java.util.List;

import model.board.Board;
import model.effect.MysteryCellLocation;
import model.player.Player;

// T-16/T-19: bundles the read-only context a PlayerStrategy needs to evaluate legal options,
// so choose() does not keep growing extra parameters as strategies get smarter.
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

    // T-19: lets BlueStrategy preview whether a candidate move would land on the Mystery Cell.
    public MysteryCellLocation getMysteryCellLocation() {
        return mysteryCellLocation;
    }

    // T-19: which roll this is within the player's current turn - lets BlueStrategy tell a
    // bonus roll (from a 6 or a capture) apart from the first roll of a brand-new turn, so its
    // cyclic piece rotation advances once per turn, not once per roll.
    public int getRollNumber() {
        return rollNumber;
    }
}
