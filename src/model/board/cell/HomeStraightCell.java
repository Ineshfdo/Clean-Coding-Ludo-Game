package model.board.cell;
import config.constant.BoardConstants;
import config.enums.PlayerColor;
import model.player.PlayerColorLabels;

// One of the 5 color-owned cells between a color's Approach cell and its Home.
public final class HomeStraightCell {

    private final PlayerColor color;
    private final int indexFromApproach;

    public HomeStraightCell(PlayerColor color, int indexFromApproach) {
        this.color = color;
        this.indexFromApproach = indexFromApproach;
    }

    public PlayerColor getColor() {
        return color;
    }

    public int getIndexFromApproach() {
        return indexFromApproach;
    }

    public boolean isLastCellBeforeHome() {
        return indexFromApproach == BoardConstants.CELLS_PER_HOME_STRAIGHT - 1;
    }

    @Override
    public String toString() {
        return PlayerColorLabels.displayNameOf(color) + "HomePath" + indexFromApproach;
    }
}
