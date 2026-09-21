package model.player;

import model.piece.Piece;

// What a piece needs before it may leave the track for its HomeStraight.
public interface HomeEntryPolicy {

    boolean forbidsEntry(Piece piece);
}
