package player;

import direction.MovementDirectionStrategy;
import ludoboard.Board;
import ludoboard.HomeStraightCell;

// T-4/T-16/T-18: how many cells (including any still-needed extra laps around the board and
// the HomeStraight itself) a piece has left before reaching Home, using its own direction.
// Shared by LongestDistanceBlockDirectionStrategy (find the block's longest journey) and
// YellowStrategy (find the shortest one).
public final class RemainingHomeDistance {

    private RemainingHomeDistance() {
    }

    public static int forPiece(Piece piece, Board board) {
        if (piece.isHome()) {
            return 0;
        }
        if (piece.isOnHomeStraight()) {
            return HomeStraightCell.CELLS_PER_HOME_STRAIGHT - piece.getHomeStraightIndex();
        }

        MovementDirectionStrategy ownDirection = piece.getOriginalMovementDirectionStrategy();
        int stepsToApproach = ownDirection.stepsToApproach(piece.getTrackPosition(), piece.getColor(), board);

        int passesAfterThisCrossing = piece.getApproachPassCount() + 1;
        int requiredPasses = ownDirection.getRequiredApproachPassCount();
        int extraLapsNeeded = Math.max(0, requiredPasses - passesAfterThisCrossing);

        return stepsToApproach + extraLapsNeeded * board.getStandardCellCount()
                + HomeStraightCell.CELLS_PER_HOME_STRAIGHT;
    }
}
