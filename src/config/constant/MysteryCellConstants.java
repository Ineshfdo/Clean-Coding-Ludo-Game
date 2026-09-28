package config.constant;



public final class MysteryCellConstants {


    public static final int ALPHA_CELL_POSITION = 9;
    public static final int BETA_CELL_POSITION = 27;
    public static final int GAMMA_CELL_POSITION = 46;

    
     // The number of rounds between the first entry on the track and the first appearance of the Mystery Cell (T-10).
    public static final int REQUIRED_ROUNDS_BEFORE_SPAWN = 2;
    
    // The number of rounds that the Mystery Cell stays on one cell before it moves (T-10).
    public static final int ROUNDS_PER_LOCATION = 4;

    private MysteryCellConstants() {}
}
