package model.player.rule.block;

import config.constant.BlockadeConstants;
import config.enums.PlayerColor;
import java.util.List;
import model.board.Board;
import model.direction.MovementDirectionStrategy;
import model.player.Player;

/**
 * Stops a move in front of an opponent blockade (T-3, T-8). Landing on a blockade of the same size
 * is allowed, because that is a capture.
 */
public final class PassingBlockadeRule extends BlockadeLimitRule {

    @Override
    protected int restrict(
            PlayerColor moverColor, int fromPosition, int requestedSteps, Board board,
            List<Player> allPlayers, MovementDirectionStrategy direction, int moverBlockSize) {
        for (int stepOffset = 1; stepOffset <= requestedSteps; stepOffset++) {
            int cellPosition = direction.nextPosition(fromPosition, stepOffset, board);
            boolean isFinalStep = stepOffset == requestedSteps;

            if (isImpassableBlockade(moverColor, cellPosition, allPlayers, moverBlockSize, isFinalStep)) {
                return stepOffset - 1;
            }
        }

        return requestedSteps;
    }

    // T-8: landing on an equal-size blockade is allowed (capture).
    private static boolean isImpassableBlockade(
            PlayerColor moverColor, int position, List<Player> allPlayers, int moverBlockSize,
            boolean isFinalStep) {
        for (Player player : allPlayers) {
            if (player.getColor() == moverColor) {
                continue;
            }

            int opponentCount = player.getPiecesAt(position).size();

            if (opponentCount < BlockadeConstants.MINIMUM_BLOCKADE_SIZE) {
                continue;
            }

            boolean capturableHere = isFinalStep && opponentCount == moverBlockSize;

            if (!capturableHere) {
                return true;
            }
        }

        return false;
    }
}
