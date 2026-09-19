package model.player.strategy.blockdirection;

import java.util.List;
import model.board.Board;
import model.direction.MovementDirectionStrategy;
import model.piece.Piece;

// Strategy: chooses which direction a block travels in.
public interface BlockTravelDirectionStrategy {

    // T-4/T-5: decided once when the block forms, then reused while it stays grouped.
    MovementDirectionStrategy resolveTravelDirection(List<Piece> blockPieces, Board board);
}
