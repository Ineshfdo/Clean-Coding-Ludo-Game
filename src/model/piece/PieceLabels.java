package model.piece;

import java.util.List;
import java.util.stream.Collectors;

// Names a group of pieces as one label, e.g. "R1+R2".
public final class PieceLabels {

    private static final String MEMBER_SEPARATOR = "+";

    private PieceLabels() {
    }

    public static String joinPieceLabels(List<Piece> pieces) {
        return pieces.stream()
                .map(Piece::toString)
                .collect(Collectors.joining(MEMBER_SEPARATOR));
    }
}
