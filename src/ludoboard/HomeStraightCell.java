package ludoboard;

// One of the 5 color-owned cells between a color's Approach cell and its Home.
public final class HomeStraightCell {

    public static final int CELLS_PER_HOME_STRAIGHT = 5;

    private final PlayerColor color;
    private final int indexFromApproach;

    HomeStraightCell(PlayerColor color, int indexFromApproach) {
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
        return indexFromApproach == CELLS_PER_HOME_STRAIGHT - 1;
    }

    @Override
    public String toString() {
        return color.getDisplayName() + "HomePath" + indexFromApproach;
    }
}
