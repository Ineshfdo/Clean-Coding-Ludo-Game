package player;

import java.util.List;
import java.util.Optional;

import direction.MovementDirectionStrategy;
import ludoboard.Board;
import ludoboard.HomeStraightCell;

// T-4: a mixed-direction block travels via the member with the longest remaining distance -
// decided once when the block first moves together, then kept for as long as it stays grouped.
public final class LongestDistanceBlockDirectionStrategy implements BlockDirectionStrategy {

    // T-4/T-5: reused as-is while the block's membership stays the same size; a new arrival
    // (or a departure) changes the size, which forces a fresh, fair comparison across
    // everyone currently in the block - never merely from positions drifting round to round.
    @Override
    public MovementDirectionStrategy resolveTravelDirection(List<Piece> blockPieces, Board board) {
        int currentBlockSize = blockPieces.size();
        Optional<Piece> decidedForCurrentMembership = blockPieces.stream()
                .filter(Piece::hasAdoptedBlockDirection)
                .filter(piece -> piece.getAdoptedForBlockSize() == currentBlockSize)
                .findFirst();
        if (decidedForCurrentMembership.isPresent()) {
            return decidedForCurrentMembership.get().getMovementDirectionStrategy();
        }
        return resolveDominantPiece(blockPieces, board).getOriginalMovementDirectionStrategy();
    }

    // T-4: only consulted the FIRST time this block moves together - the member with the
    // longest remaining distance to Home sets the direction everyone else then adopts.
    private static Piece resolveDominantPiece(List<Piece> blockPieces, Board board) {
        Piece dominantPiece = blockPieces.get(0);
        int longestRemaining = remainingDistanceToHome(dominantPiece, board);

        for (Piece piece : blockPieces) {
            int remaining = remainingDistanceToHome(piece, board);
            if (remaining > longestRemaining) {
                longestRemaining = remaining;
                dominantPiece = piece;
            }
        }
        return dominantPiece;
    }

    // T-1/T-5: uses each piece's own original direction, never a block's borrowed one.
    private static int remainingDistanceToHome(Piece piece, Board board) {
        MovementDirectionStrategy ownDirection = piece.getOriginalMovementDirectionStrategy();
        int stepsToApproach =
                ownDirection.stepsToApproach(piece.getTrackPosition(), piece.getColor(), board);

        int passesAfterThisCrossing = piece.getApproachPassCount() + 1;
        int requiredPasses = ownDirection.getRequiredApproachPassCount();
        int extraLapsNeeded = Math.max(0, requiredPasses - passesAfterThisCrossing);

        return stepsToApproach + extraLapsNeeded * board.getStandardCellCount()
                + HomeStraightCell.CELLS_PER_HOME_STRAIGHT;
    }
}
