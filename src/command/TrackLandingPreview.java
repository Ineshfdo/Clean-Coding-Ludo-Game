package command;

import java.util.Optional;

import direction.MovementDirectionStrategy;
import ludoboard.Board;
import player.Piece;

// T-16: shared by MoveCommand/BlockMoveCommand to preview a track landing before executing -
// used by RedStrategy's capture/block-avoidance heuristics. A piece that would step onto or
// past its Approach cell has no single predictable landing cell (it may enter HomeStraight
// instead), so this returns empty for that case.
final class TrackLandingPreview {

    private TrackLandingPreview() {
    }

    static Optional<Integer> resolve(
            Piece piece, int steps, Board board, MovementDirectionStrategy travelDirection) {
        if (!piece.isOnTrack()) {
            return Optional.empty();
        }
        int stepsToApproach = travelDirection.stepsToApproach(piece.getTrackPosition(), piece.getColor(), board);
        if (steps > stepsToApproach) {
            return Optional.empty();
        }
        return Optional.of(travelDirection.nextPosition(piece.getTrackPosition(), steps, board));
    }
}
