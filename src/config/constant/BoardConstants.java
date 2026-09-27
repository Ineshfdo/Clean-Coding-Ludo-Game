package config.constant;


/**
 * Constants of the board layout: the size of the track and of the HomeStraight, the Approach and
 * Entry cells of every colour, and two marker values.
 */
public final class BoardConstants {

    /**
     * Number of cells on the shared track.
     */
    public static final int STANDARD_CELL_COUNT = 52;
    /**
     * Number of cells in each HomeStraight.
     */
    public static final int CELLS_PER_HOME_STRAIGHT = 5;

    /**
     * Number of pieces of each player.
     */
    public static final int PIECES_PER_PLAYER = 4;
    /**
     * Start index of a piece that enters the HomeStraight from the track. It lies one before the
     * first HomeStraight cell, which has index 0.
     */
    public static final int BEFORE_FIRST_HOME_STRAIGHT_CELL = -1;

    /**
     * Marker for a piece that is not on the track, and for a Mystery Cell that has not appeared
     * yet.
     */
    public static final int NO_TRACK_POSITION = -1;

    /**
     * Track position of the Approach cell of Yellow.
     */
    public static final int YELLOW_APPROACH_POSITION = 0;
    /**
     * Track position of the Approach cell of Blue.
     */
    public static final int BLUE_APPROACH_POSITION = 13;
    /**
     * Track position of the Approach cell of Red.
     */
    public static final int RED_APPROACH_POSITION = 26;
    /**
     * Track position of the Approach cell of Green.
     */
    public static final int GREEN_APPROACH_POSITION = 39;

    /**
     * Track position of the Entry cell of Yellow.
     */
    public static final int YELLOW_ENTRY_POSITION = 2;
    /**
     * Track position of the Entry cell of Blue.
     */
    public static final int BLUE_ENTRY_POSITION = 15;
    /**
     * Track position of the Entry cell of Red.
     */
    public static final int RED_ENTRY_POSITION = 28;
    /**
     * Track position of the Entry cell of Green.
     */
    public static final int GREEN_ENTRY_POSITION = 41;

    private BoardConstants() {}
}
