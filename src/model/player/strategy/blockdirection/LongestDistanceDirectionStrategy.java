package model.player.strategy.blockdirection;

import java.util.List;
import java.util.Optional;
import model.board.Board;
import model.piece.Piece;
import model.position.MovementDirectionStrategy;
import model.position.RemainingHomeDistance;

// T-4: a mixed block travels via the member with the longest remaining distance.
public final class LongestDistanceDirectionStrategy implements BlockTravelDirectionStrategy {

    // T-4/T-5: reused while the block's size is unchanged; a new size forces a fresh comparison.
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

    // T-4: the member farthest from Home sets the direction everyone adopts.
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
