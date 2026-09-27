package model.board;

import config.enums.PlayerColor;
import model.board.cell.HomeStraightCell;
import model.board.cell.StandardCell;

/**
 * Contract of the board geometry. Player, Piece and the rules depend on this interface and not on
 * the concrete LudoBoard. The shared track is a circle of numbered cells, and every colour has its
 * own HomeStraight.
 */
public interface Board {

    /**
     * Gives the size of the shared track.
     *
     * @return the number of cells on the shared track
     */
    int getStandardCellCount();

    /**
     * Gives the size of a HomeStraight.
     *
     * @return the number of cells in each HomeStraight
     */
    int getHomeStraightLength();

    /**
     * Looks up a cell of the shared track.
     *
     * @param position the position of the cell on the track
     * @return the cell at that position
     */
    StandardCell getStandardCell(int position);

    /**
     * Looks up a cell of a HomeStraight.
     *
     * @param color the colour that owns the HomeStraight
     * @param indexFromApproach index of the cell, counted from the Approach cell; 0 is the first
     *     cell
     * @return the cell of that HomeStraight
     */
    HomeStraightCell getHomeStraightCell(PlayerColor color, int indexFromApproach);

    /**
     * Finds the Approach cell of a colour.
     *
     * @param color the colour to look up
     * @return the track position of its Approach cell
     */
    int getApproachCellPosition(PlayerColor color);

    /**
     * Finds the cell where the pieces of a colour enter the track.
     *
     * @param color the colour to look up
     * @return the track position of its Entry cell
     */
    int getEntryCellPosition(PlayerColor color);

    /**
     * Moves clockwise along the track and wraps around at the end.
     *
     * @param currentPosition the track position to start from
     * @param steps the number of steps to move
     * @return the track position after the move
     */
    int getPositionAfterMoving(int currentPosition, int steps);

    /**
     * Moves counter-clockwise along the track and wraps around at the start.
     *
     * @param currentPosition the track position to start from
     * @param steps the number of steps to move
     * @return the track position after the move
     */
    int getPositionAfterMovingBackward(int currentPosition, int steps);

    /**
     * Counts the clockwise steps from one cell to another, wrapping around the track.
     *
     * @param fromPosition the track position to start from
     * @param toPosition the track position to reach
     * @return the number of steps; 0 when both positions are the same
     */
    int getForwardDistance(int fromPosition, int toPosition);
}
