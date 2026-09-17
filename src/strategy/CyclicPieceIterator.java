package strategy;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import player.Piece;

// T-19/Iterator: cycles through a player's own 4 pieces forever, in a fixed rotation -
// B1 -> B2 -> B3 -> B4 -> B1 -> ... - backing BlueStrategy's round-robin piece selection
// (Behavior 1). Unlike a normal Iterator, it never runs out - hasNext() only reports whether
// there is anything to cycle through at all.
public final class CyclicPieceIterator implements Iterator<Piece> {

    private final List<Piece> pieces;
    private int nextPieceIndex;

    public CyclicPieceIterator(List<Piece> pieces) {
        this(pieces, null);
    }

    // T-19: resumes the rotation immediately after lastMovedPiece, so it continues exactly
    // where the last ACTUAL move left off - even when a Mystery Cell override (Behavior 2/3)
    // moved a different piece than the plain rotation would have picked. A null
    // lastMovedPiece starts the rotation at the beginning (B1).
    public CyclicPieceIterator(List<Piece> pieces, Piece lastMovedPiece) {
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
