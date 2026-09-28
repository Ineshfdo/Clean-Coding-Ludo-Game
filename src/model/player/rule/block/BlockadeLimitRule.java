package model.player.rule.block;

import config.enums.PlayerColor;
import java.util.List;
import model.board.Board;
import model.direction.MovementDirectionStrategy;
import model.player.Player;
import model.rule.ChainedRule;

/**
 Chain of Responsibility where each rule may limit the number of steps that a move is allowed to take, for example because of an opponent blockade (T-3).
 Each rule works on the steps that the rule before it allowed.
 */
public abstract class BlockadeLimitRule extends ChainedRule<BlockadeLimitRule> {

    /**
     Asks this rule and then the next rules of the chain.
     @param moverColor the colour of the moving player
     @param fromPosition the track position where the move starts
     @param requestedSteps the number of steps that the move wants to take
     @param board the board the pieces move on
     @param allPlayers all players of the game
     @param direction the direction in which the piece or blockade travels
     @param moverBlockSize the number of pieces that move together
     @return the number of steps that are allowed
     */
    public final int limitSteps(
            PlayerColor moverColor, int fromPosition, int requestedSteps, Board board,
            List<Player> allPlayers, MovementDirectionStrategy direction, int moverBlockSize) {
        int allowedSteps = restrict(
                moverColor, fromPosition, requestedSteps, board, allPlayers, direction, moverBlockSize);

        return getNextRule()
                .map(nextRule -> nextRule.limitSteps(
                        moverColor, fromPosition, allowedSteps, board, allPlayers, direction,
                        moverBlockSize))
                .orElse(allowedSteps);
    }

    /**
     Limits the steps according to the condition of this rule alone.
     @param moverColor the colour of the moving player
     @param fromPosition the track position where the move starts
     @param requestedSteps the number of steps that the move wants to take
     @param board the board the pieces move on
     @param allPlayers all players of the game
     @param direction the direction in which the piece or blockade travels
     @param moverBlockSize the number of pieces that move together
     @return the number of steps that this rule allows
     */
    protected abstract int restrict(
            PlayerColor moverColor, int fromPosition, int requestedSteps, Board board,
            List<Player> allPlayers, MovementDirectionStrategy direction, int moverBlockSize);
}
