package model.effect.mysterycell;

/**
 * Read-only view of the location of the Mystery Cell (T-19). Rules and strategies only need to look
 * at it, so they cannot move it (ISP).
 */
public interface MysteryCellLocation {

    /**
     * Tells whether the Mystery Cell is on the board.
     *
     * @return true once it has appeared
     */
    boolean isActive();

    /**
     * Gives the cell of the Mystery Cell.
     *
     * @return the track position, or -1 before the Mystery Cell has appeared
     */
    int getCurrentCellPosition();
}
