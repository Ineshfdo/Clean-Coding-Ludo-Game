package player;

import java.util.List;

import direction.MovementDirectionStrategy;
import ludoboard.Board;
import ludoboard.HomeStraightCell;

// T-1: block travels via the member with the longest remaining distance; shared by two callers.
public final class BlockTravelDirection {

    private BlockTravelDirection() {
    }

    public static MovementDirectionStrategy resolve(List<Piece> blockPieces, Board board) {
        Piece dominantPiece = blockPieces.get(0);
        int longestRemaining = remainingDistanceToHome(dominantPiece, board);

        for (Piece piece : blockPieces) {
            int remaining = remainingDistanceToHome(piece, board);
            if (remaining > longestRemaining) {
                longestRemaining = remaining;
                dominantPiece = piece;
            }
        }
        return dominantPiece.getMovementDirectionStrategy();
    }

    // T-1: short of its required passes, a piece must lap the track again before Home.
    private static int remainingDistanceToHome(Piece piece, Board board) {
        MovementDirectionStrategy ownDirection = piece.getMovementDirectionStrategy();
        int stepsToApproach =
                ownDirection.stepsToApproach(piece.getTrackPosition(), piece.getColor(), board);

        int passesAfterThisCrossing = piece.getApproachPassCount() + 1;
        int requiredPasses = ownDirection.getRequiredApproachPassCount();
        int extraLapsNeeded = Math.max(0, requiredPasses - passesAfterThisCrossing);

        return stepsToApproach + extraLapsNeeded * board.getStandardCellCount()
                + HomeStraightCell.CELLS_PER_HOME_STRAIGHT;
    }
}
