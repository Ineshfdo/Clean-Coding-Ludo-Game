package model.player.command.move;

import java.util.Optional;
import model.board.Board;
import model.direction.MovementDirectionStrategy;
import model.piece.Piece;

// T-16: previews a track landing cell without moving (used by RedStrategy).
// Empty if the piece would reach or pass its Approach cell.
final class TrackLandingFinder {

    private TrackLandingFinder() {
    }

    static Optional<Integer> findLandingPosition(
            Piece piece, int steps, Board board, MovementDirectionStrategy travelDirection) {
        if (!piece.isOnTrack()) {
            return Optional.empty();
        }

        int stepsToApproach = travelDirection.countStepsToApproach(piece.getTrackPosition(), piece.getColor(), board);

        if (steps > stepsToApproach) {
            return Optional.empty();
        }

        return Optional.of(travelDirection.nextPosition(piece.getTrackPosition(), steps, board));
    }
}
