package player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import direction.MovementDirectionStrategy;

// T-4: classifies whether a block's members share one direction or travel opposite ways.
public enum BlockDirectionType {

    SAME_DIRECTION("Same-Direction"),
    OPPOSITE_DIRECTION("Opposite-Direction");

    private static final int MIN_MIXED_BLOCK_SIZE = 2;

    private final String label;

    BlockDirectionType(String label) {
        this.label = label;
    }

    public static BlockDirectionType classify(List<Piece> blockPieces) {
        if (blockPieces.size() < MIN_MIXED_BLOCK_SIZE) {
            return SAME_DIRECTION;
        }
        Set<MovementDirectionStrategy> directions = blockPieces.stream()
                .map(Piece::getMovementDirectionStrategy)
                .collect(Collectors.toSet());
        return directions.size() > 1 ? OPPOSITE_DIRECTION : SAME_DIRECTION;
    }

    public String getLabel() {
        return label;
    }
}
