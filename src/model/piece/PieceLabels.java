package model.piece;

import java.util.List;
import java.util.stream.Collectors;

/**
 Names a group of pieces with one label, for example R1+R2.
 */
public final class PieceLabels {

    private static final String MEMBER_SEPARATOR = "+";

    private PieceLabels() {
    }

    /**
     Joins the names of the pieces with a plus sign.
     @param pieces the pieces to name
     @return the joined label, or an empty text when there are no pieces
     */
    public static String joinPieceLabels(List<Piece> pieces) {
        return pieces.stream()
                .map(Piece::toString)
                .collect(Collectors.joining(MEMBER_SEPARATOR));
    }
}
