package rule;

import java.util.List;

import direction.MovementDirectionStrategy;
import ludoboard.Board;
import ludoboard.PlayerColor;
import player.Piece;
import player.Player;

// T-3: 2+ same-color pieces sharing a cell block opponents; movement is capped just before it.
public final class OpponentBlockadeRule extends BlockadeRule {

    private static final int BLOCKADE_PIECE_COUNT = 2;

    @Override
    protected int restrict(
            PlayerColor moverColor, int fromPosition, int requestedSteps, Board board,
            List<Player> allPlayers, MovementDirectionStrategy direction) {
        for (int stepOffset = 1; stepOffset <= requestedSteps; stepOffset++) {
            int cellPosition = direction.nextPosition(fromPosition, stepOffset, board);
            if (isOpponentBlockade(moverColor, cellPosition, allPlayers)) {
                return stepOffset - 1;
            }
        }
        return requestedSteps;
    }

    private static boolean isOpponentBlockade(
            PlayerColor moverColor, int position, List<Player> allPlayers) {
        for (Player player : allPlayers) {
            if (player.getColor() == moverColor) {
                continue;
            }
            if (countPiecesAt(player, position) >= BLOCKADE_PIECE_COUNT) {
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
