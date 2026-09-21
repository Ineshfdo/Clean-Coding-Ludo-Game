package model.direction;

import config.constant.BoardConstants;
import model.board.Board;
import model.piece.Piece;

// T-4/T-16/T-18: cells a piece has left to reach Home, using its own direction.
public final class RemainingHomeDistance {

    private RemainingHomeDistance() {
    }

    public static int forPiece(Piece piece, Board board) {
        if (piece.isHome()) {
            return 0;
        }

        if (piece.isOnHomeStraight()) {
            return BoardConstants.CELLS_PER_HOME_STRAIGHT - piece.getHomeStraightIndex();
        }

        MovementDirectionStrategy ownDirection = piece.getOriginalMovementDirection();
        int stepsToApproach = ownDirection.countStepsToApproach(piece.getTrackPosition(), piece.getColor(), board);

        // Standing on Approach, its arrival is already counted, so only full laps remain.
        int nextCrossingPasses = stepsToApproach > 0 ? 1 : 0;
        int passesAfterThisCrossing = piece.getApproachPassCount() + nextCrossingPasses;
        int requiredPasses = ownDirection.getRequiredApproachPassCount();
        int extraLapsNeeded = Math.max(0, requiredPasses - passesAfterThisCrossing);

        return stepsToApproach + extraLapsNeeded * board.getStandardCellCount()
                + BoardConstants.CELLS_PER_HOME_STRAIGHT;
    }
}
