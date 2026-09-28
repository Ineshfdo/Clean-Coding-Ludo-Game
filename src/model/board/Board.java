package model.board;

import config.enums.PlayerColor;
import model.board.cell.HomeStraightCell;
import model.board.cell.StandardCell;


 // Contract of the board, used instead of the concrete LudoBoard.


public interface Board {

        /**
         * Gives the size of the shared track.
         
         * @return the number of cells on the shared track
        */
    int getStandardCellCount();

        /**
         * Gives the size of a HomeStraight.
         
         * @return the number of cells in each HomeStraight
        */
    int getHomeStraightLength();

        /**
         * Looks up a cell of the shared track.
         
         * @param position the position of the cell on the track
         * @return the cell at that position
        */
    StandardCell getStandardCell(int position);

        /**
         * Returns the specific Home Straight cell for a color at a given index from the Approach.
         
         * @param color the colour that owns the HomeStraight
         * @param indexFromApproach index of the cell, counted from the Approach cell; 0 is the first
         *     cell
         * @return the cell of that HomeStraight
        */
    HomeStraightCell getHomeStraightCell(PlayerColor color, int indexFromApproach);

        /**
         * Finds the Approach cell of a colour.
         
         * @param color the colour to look up
         * @return the track position of its Approach cell
        */
    int getApproachCellPosition(PlayerColor color);

        /**
         * Finds the cell where the pieces of a colour enter the track.
         
         * @param color the colour to look up
         * @return the track position of its Entry cell
        */
    int getEntryCellPosition(PlayerColor color);

        /**
         * Calculates the new track position after moving clockwize by a given number of steps.
         
         * @param currentPosition the track position to start from
         * @param steps the number of steps to move
         * @return the track position after the move
        */
    int getPositionAfterMoving(int currentPosition, int steps);

        /**
         * Calculates the new track position after moving backward counter-clockwise by given steps  
         
         * @param currentPosition the track position to start from
         * @param steps the number of steps to move
         * @return the track position after the move
        */
    int getPositionAfterMovingBackward(int currentPosition, int steps);

        /**
         * Calculates steps needed to move forward from one position to another
         
         * @param fromPosition the track position to start from
         * @param toPosition the track position to reach
         * @return the number of steps; 0 when both positions are the same
        */

    int getForwardDistance(int fromPosition, int toPosition);
}
