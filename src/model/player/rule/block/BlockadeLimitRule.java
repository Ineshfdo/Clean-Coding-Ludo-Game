package model.player.rule.block;

import config.enums.PlayerColor;
import java.util.List;
import model.board.Board;
import model.direction.MovementDirectionStrategy;
import model.player.Player;

// Chain of Responsibility: each rule may limit the allowed steps.
public abstract class BlockadeLimitRule {

    private BlockadeLimitRule nextRule;

    public final void setNext(BlockadeLimitRule nextRule) {
        this.nextRule = nextRule;
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
                        moverBlockSize
                );
        }

        protected abstract int restrict(
            PlayerColor moverColor, int fromPosition, int requestedSteps, Board board,
            List<Player> allPlayers, MovementDirectionStrategy direction, int moverBlockSize
        );
}
