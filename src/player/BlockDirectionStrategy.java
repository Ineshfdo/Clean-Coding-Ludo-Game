package player;

import java.util.List;

import ludoboard.Board;

// Strategy: chooses which member's direction a block travels in.
public interface BlockDirectionStrategy {

    Piece resolveDominantPiece(List<Piece> blockPieces, Board board);
}
