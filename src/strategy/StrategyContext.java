package strategy;

import java.util.List;

import ludoboard.Board;
import player.Player;

// T-16: bundles the read-only context a PlayerStrategy needs to evaluate legal options, so
// choose() does not keep growing extra parameters as strategies get smarter.
public final class StrategyContext {

    private final Player player;
    private final List<Player> allPlayers;
    private final Board board;

    public StrategyContext(Player player, List<Player> allPlayers, Board board) {
        this.player = player;
        this.allPlayers = allPlayers;
        this.board = board;
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
}
