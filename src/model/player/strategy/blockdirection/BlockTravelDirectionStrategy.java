package model.player.strategy.blockdirection;

import java.util.List;
import model.board.Board;
import model.direction.MovementDirectionStrategy;
import model.piece.Piece;

/**
 Strategy that chooses the direction in which a blockade travels.
 */
public interface BlockTravelDirectionStrategy {

    /**
     Chooses the direction.
     The choice is made once when the blockade forms, and it is reused while the blockade stays grouped (T-4, T-5).
     @param blockPieces the pieces of the blockade
     @param board the board that gives the track
     @return the direction in which the blockade travels
     */
    MovementDirectionStrategy resolveTravelDirection(List<Piece> blockPieces, Board board);
}
