package config.constant;


/**
 * Constants of the Mystery Cell: its teleport cells and its timing.
 */
public final class MysteryCellConstants {

    /**
     * Track position of the Alpha cell, a teleport destination.
     */
    public static final int ALPHA_CELL_POSITION = 9;
    /**
     * Track position of the Beta cell, a teleport destination.
     */
    public static final int BETA_CELL_POSITION = 27;
    /**
     * Track position of the Gamma cell, a teleport destination.
     */
    public static final int GAMMA_CELL_POSITION = 46;

    /**
     * The number of rounds between the first entry on the track and the first appearance of the
     * Mystery Cell (T-10).
     */
    public static final int REQUIRED_ROUNDS_BEFORE_SPAWN = 2;
    /**
     * The number of rounds that the Mystery Cell stays on one cell before it moves (T-10).
     */
    public static final int ROUNDS_PER_LOCATION = 4;

    private MysteryCellConstants() {}
}
