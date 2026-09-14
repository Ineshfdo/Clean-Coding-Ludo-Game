package rule;

import java.util.List;

import ludoboard.Board;
import ludoboard.PlayerColor;
import player.Player;

// Chain of Responsibility: each rule may further restrict how many
// of the requested steps are actually allowed, then defers onward.
public abstract class BlockadeRule {

    private BlockadeRule nextRule;

    public final BlockadeRule setNext(BlockadeRule nextRule) {
        this.nextRule = nextRule;
        return nextRule;
    }

    public final int limitSteps(
            PlayerColor moverColor, int fromPosition, int requestedSteps,
            Board board, List<Player> allPlayers) {
        int allowedSteps = restrict(moverColor, fromPosition, requestedSteps, board, allPlayers);
        return nextRule == null
                ? allowedSteps
                : nextRule.limitSteps(moverColor, fromPosition, allowedSteps, board, allPlayers);
    }

    protected abstract int restrict(
            PlayerColor moverColor, int fromPosition, int requestedSteps,
            Board board, List<Player> allPlayers);
}
