package model.player.strategy.blockdirection;

import java.util.List;
import java.util.Optional;
import model.board.Board;
import model.direction.MovementDirectionStrategy;
import model.piece.Piece;
import model.piece.RemainingHomeDistance;

/**
 A blockade travels in the original direction of its member that is farthest from Home (T-4).
 The choice is reused while the blockade keeps its size, and a new size makes a new comparison.
 */
public final class LongestDistanceDirectionStrategy implements BlockTravelDirectionStrategy {

    // T-4/T-5: reused while the block's size is unchanged; a new size forces a fresh comparison.
    @Override
    public MovementDirectionStrategy resolveTravelDirection(List<Piece> blockPieces, Board board) {
        int currentBlockSize = blockPieces.size();
        Optional<Piece> pieceWithAdoptedDirection = blockPieces.stream()
                .filter(Piece::hasAdoptedBlockDirection)
                .filter(piece -> piece.getAdoptedForBlockSize() == currentBlockSize)
                .findFirst();

        if (pieceWithAdoptedDirection.isPresent()) {
            return pieceWithAdoptedDirection.get().getMovementDirection();
        }

        return resolveDominantPiece(blockPieces, board).getOriginalMovementDirection();
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
