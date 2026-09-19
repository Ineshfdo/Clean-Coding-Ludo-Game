package model.player.strategy;
import model.piece.Piece;
import model.position.RemainingHomeDistance;

import java.util.List;
import java.util.Optional;

import model.position.MovementDirectionStrategy;
import model.board.Board;

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
        int longestRemaining = RemainingHomeDistance.forPiece(dominantPiece, board);

        for (Piece piece : blockPieces) {
            int remaining = RemainingHomeDistance.forPiece(piece, board);
            if (remaining > longestRemaining) {
                longestRemaining = remaining;
                dominantPiece = piece;
            }
        }
        return dominantPiece;
    }
}
