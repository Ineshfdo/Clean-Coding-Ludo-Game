package model.board;

/**
 * The fixed cells to which a Mystery Cell teleports pieces (T-11): Alpha, Beta and Gamma. They are
 * the same for every colour.
 */
public interface MysteryCellPositions {

    /**
     * Gives the Alpha cell.
     *
     * @return the track position of the Alpha cell
     */
    int getAlphaCellPosition();

    /**
     * Gives the Beta cell.
     *
     * @return the track position of the Beta cell
     */
    int getBetaCellPosition();

    /**
     * Gives the Gamma cell.
     *
     * @return the track position of the Gamma cell
     */
    int getGammaCellPosition();
}
