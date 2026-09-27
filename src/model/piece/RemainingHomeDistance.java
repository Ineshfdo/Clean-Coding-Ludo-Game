package model.piece;

import model.board.Board;
import model.direction.MovementDirectionStrategy;

/**
 * Counts how many cells a piece still has to travel to reach Home. The count uses the original
 * direction of the piece (T-4, T-16, T-18).
 */
public final class RemainingHomeDistance {

    private RemainingHomeDistance() {
    }

    /**
     * Counts the remaining cells of a piece.
     *
     * @param piece the piece to measure
     * @param board the board that gives the track and the HomeStraight
     * @return the number of cells left; 0 when the piece is already Home
     */
    public static int forPiece(Piece piece, Board board) {
        if (piece.isHome()) {
            return 0;
        }

        if (piece.isOnHomeStraight()) {
            return board.getHomeStraightLength() - piece.getHomeStraightIndex();
        }

        MovementDirectionStrategy ownDirection = piece.getOriginalMovementDirection();
        int stepsToApproach = ownDirection.countStepsToApproach(piece.getTrackPosition(), piece.getColor(), board);

        // Standing on Approach, its arrival is already counted, so only full laps remain.
        int nextCrossingPasses = stepsToApproach > 0 ? 1 : 0;
        int passesAfterThisCrossing = piece.getApproachPassCount() + nextCrossingPasses;
        int requiredPasses = ownDirection.getRequiredApproachPassCount();
        int extraLapsNeeded = Math.max(0, requiredPasses - passesAfterThisCrossing);

        return stepsToApproach + extraLapsNeeded * board.getStandardCellCount()
                + board.getHomeStraightLength();
    }
}
