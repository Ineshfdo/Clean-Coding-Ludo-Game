package model.player.rule.block;

import config.constant.BlockadeConstants;
import config.enums.PlayerColor;
import java.util.List;
import model.board.Board;
import model.piece.Piece;
import model.player.Player;
import model.position.MovementDirectionStrategy;

// T-3/T-8: 2+ same-color pieces block opponents, except an equal-size capture.
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

            long opponentCount = countPiecesAt(player, position);

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

    private static long countPiecesAt(Player player, int position) {
        return player.getPieces().stream()
            .filter(Piece::isOnTrack)
            .filter(piece -> piece.getTrackPosition() == position)
            .count();
    }
}
