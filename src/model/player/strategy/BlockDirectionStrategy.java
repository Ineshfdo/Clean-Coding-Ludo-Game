package model.player.strategy;
import model.piece.Piece;

import java.util.List;

import model.position.MovementDirectionStrategy;
import model.board.Board;

// Strategy: chooses which direction a block travels in.
public interface BlockDirectionStrategy {

    // T-4/T-5: decided once, when the block first moves together as a group, then reused for
    // as long as it stays grouped - never recalculated on every later round or display.
    MovementDirectionStrategy resolveTravelDirection(List<Piece> blockPieces, Board board);
}
