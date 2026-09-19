package model.player.strategy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import config.enums.BlockDirectionType;
import model.piece.Piece;
import model.position.MovementDirectionStrategy;

// T-4/T-5: classifies a block by each piece's own original direction, immune to any adopted
// block one - and its display text. Kept out of BlockDirectionType itself so the enum stays a
// pure list of constants.
public final class BlockDirectionClassifier {

    private static final int MIN_MIXED_BLOCK_SIZE = 2;

    private BlockDirectionClassifier() {
    }

    public static BlockDirectionType classify(List<Piece> blockPieces) {
        if (blockPieces.size() < MIN_MIXED_BLOCK_SIZE) {
            return BlockDirectionType.SAME_DIRECTION;
        }
        Set<MovementDirectionStrategy> directions = blockPieces.stream()
                .map(Piece::getOriginalMovementDirectionStrategy)
                .collect(Collectors.toSet());
        return directions.size() > 1 ? BlockDirectionType.OPPOSITE_DIRECTION : BlockDirectionType.SAME_DIRECTION;
    }

    public static String labelOf(BlockDirectionType type) {
        return switch (type) {
            case SAME_DIRECTION -> "Same-Direction";
            case OPPOSITE_DIRECTION -> "Opposite-Direction";
        };
    }
}
