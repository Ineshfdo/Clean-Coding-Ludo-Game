package rule;

import java.util.List;

import direction.MovementDirectionStrategy;
import ludoboard.Board;
import ludoboard.PlayerColor;
import player.Player;

// Chain of Responsibility: each rule may further restrict the allowed steps, then defers.
public abstract class BlockadeRule {

    private BlockadeRule nextRule;

    public final BlockadeRule setNext(BlockadeRule nextRule) {
        this.nextRule = nextRule;
        return nextRule;
    }

    public final int limitSteps(
            PlayerColor moverColor, int fromPosition, int requestedSteps, Board board,
            List<Player> allPlayers, MovementDirectionStrategy direction, int moverBlockSize) {
        int allowedSteps = restrict(
                moverColor, fromPosition, requestedSteps, board, allPlayers, direction, moverBlockSize);
        return nextRule == null
                ? allowedSteps
                : nextRule.limitSteps(
                        moverColor, fromPosition, allowedSteps, board, allPlayers, direction,
                        moverBlockSize);
    }

    protected abstract int restrict(
            PlayerColor moverColor, int fromPosition, int requestedSteps, Board board,
            List<Player> allPlayers, MovementDirectionStrategy direction, int moverBlockSize);
}
