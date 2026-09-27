package model.player;

import model.piece.Piece;

/**
 * What a piece needs before it may leave the track for its HomeStraight.
 */
public interface HomeEntryPolicy {

    /**
     * Checks a piece that has reached its Approach cell.
     *
     * @param piece the piece that wants to enter its HomeStraight
     * @return true when the piece may not enter yet
     */
    boolean forbidsEntry(Piece piece);
}
