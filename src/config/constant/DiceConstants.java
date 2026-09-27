package config.constant;


/**
 * Constants of the die and of the sixes.
 */
public final class DiceConstants {

    /**
     * The lowest value of the die.
     */
    public static final int LOWEST_FACE_VALUE = 1;
    /**
     * The highest value of the die.
     */
    public static final int HIGHEST_FACE_VALUE = 6;

    /**
     * The roll that brings a piece out of Base and gives the player another roll.
     */
    public static final int SIX_ROLL_VALUE = 6;

    /**
     * The number of sixes in a row at which the roll is void (rule 4).
     */
    public static final int THIRD_CONSECUTIVE_SIX_COUNT = 3;

    private DiceConstants() {}
}
