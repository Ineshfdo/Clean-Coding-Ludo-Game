package model.player.strategy.helper;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import model.piece.Piece;

// T-19: cycles endlessly through a player's pieces in fixed rotation (B1 -> B2 -> B3 -> B4).
public final class BluePieceRotationIterator implements Iterator<Piece> {

    private final List<Piece> pieces;
    private int nextPieceIndex;

    public BluePieceRotationIterator(List<Piece> pieces) {
        this(pieces, null);
    }

    // T-19: resumes after the last moved piece; null starts at the beginning (B1).
    public BluePieceRotationIterator(List<Piece> pieces, Piece lastMovedPiece) {
        this.pieces = pieces;
        this.nextPieceIndex = lastMovedPiece == null
                ? 0
                : (pieces.indexOf(lastMovedPiece) + 1) % pieces.size();
    }

    @Override
    public boolean hasNext() {
        return !pieces.isEmpty();
    }

    @Override
    public Piece next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No pieces to cycle through");
        }

        Piece nextPiece = pieces.get(nextPieceIndex);
        nextPieceIndex = (nextPieceIndex + 1) % pieces.size();

        return nextPiece;
    }
}
