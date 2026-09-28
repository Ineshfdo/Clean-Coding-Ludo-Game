package model.board.cell;

import config.constant.BoardConstants;
import config.enums.PlayerColor;
import utils.color.PlayerColorNames;

// One of six cells leading from a colour's Approach cell to Home.

public final class HomeStraightCell {
    
    private final PlayerColor color;
    // Position of this cell within the home straight from the Approach.
    private final int indexFromApproach;

        /**
         * Creates a HomeStraight cell.
         *
         * @param color the colour that owns the cell
         * @param indexFromApproach the index of the cell, counted from the Approach cell
        */

    public HomeStraightCell(PlayerColor color, int indexFromApproach) {
        this.color = color;
        this.indexFromApproach = indexFromApproach;
    }

        /**
         * Gives the owner of the cell.
         *
         * @return the colour that owns the cell
         */

    public PlayerColor getColor() {
        return color;
    }

        /**
         * Gives the place of the cell on the HomeStraight.
         *
         * @return the index, counted from the Approach cell; 0 is the first cell
         */
    
    public int getIndexFromApproach() {
        return indexFromApproach;
    }

        /**
         * Tells whether this is the last cell before Home.
         *
         * @return true for the last cell of the HomeStraight
         */

    public boolean isLastCellBeforeHome() {
        return indexFromApproach == BoardConstants.CELLS_PER_HOME_STRAIGHT - 1;
    }

    // Builds a readable name for this cell from its colour and index.
    @Override
    public String toString() {
        return PlayerColorNames.displayNameOf(color) + "HomePath" + indexFromApproach;
    }
}
