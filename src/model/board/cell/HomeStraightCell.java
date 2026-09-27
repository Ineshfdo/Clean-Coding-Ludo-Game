package model.board.cell;

import config.constant.BoardConstants;
import config.enums.PlayerColor;
import utils.color.PlayerColorNames;

/**
 * One of the cells between the Approach cell of a colour and its Home. It belongs to one colour and
 * has an index, counted from the Approach cell.
 */
public final class HomeStraightCell {

    private final PlayerColor color;
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

    @Override
    public String toString() {
        return PlayerColorNames.displayNameOf(color) + "HomePath" + indexFromApproach;
    }
}
