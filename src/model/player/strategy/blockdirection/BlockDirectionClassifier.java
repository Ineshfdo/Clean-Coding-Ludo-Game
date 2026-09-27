package model.player.strategy.blockdirection;

import config.constant.BlockadeConstants;
import config.enums.BlockDirectionType;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import model.direction.MovementDirectionStrategy;
import model.piece.Piece;

/**
 * Classifies a blockade by the original directions of its pieces, and gives the type a label (T-4,
 * T-5).
 */
public final class BlockDirectionClassifier {

    private BlockDirectionClassifier() {
    }

    /**
     * Finds the type of a blockade.
     *
     * @param blockPieces the pieces of the blockade
     * @return the opposite-direction type when the pieces have different original directions,
     *     otherwise the same-direction type
     */
    public static BlockDirectionType classify(List<Piece> blockPieces) {
        if (blockPieces.size() < BlockadeConstants.MINIMUM_BLOCKADE_SIZE) {
            return BlockDirectionType.SAME_DIRECTION;
        }

        Set<MovementDirectionStrategy> directions = blockPieces.stream()
                .map(Piece::getOriginalMovementDirection)
                .collect(Collectors.toSet());

        return directions.size() > 1 ? BlockDirectionType.OPPOSITE_DIRECTION : BlockDirectionType.SAME_DIRECTION;
    }

    /**
     * Gives the text shown on the console.
     *
     * @param type the type of the blockade
     * @return the label, for example Same-Direction
     */
    public static String labelOf(BlockDirectionType type) {
        return switch (type) {
            case SAME_DIRECTION -> "Same-Direction";
            case OPPOSITE_DIRECTION -> "Opposite-Direction";
        };
    }
}
