package config.constant;

/**
 * Constants of the turn rules.
 */
public final class TurnConstants {

    /**
     * The number of the first dice roll of a turn, which tells it apart from the bonus rolls that
     * follow.
     */
    public static final int FIRST_ROLL_OF_TURN = 1;

    /**
     * The number of rolls in a row, taken while every opponent is already Home, after which the
     * home gate opens (T-7).
     */
    public static final int CONSECUTIVE_ROLLS_TO_OPEN_HOME_GATE = 3;

    /**
     * The number of opponent pieces that a piece must capture before it may enter its HomeStraight
     * (T-7).
     */
    public static final int REQUIRED_CAPTURES_TO_ENTER_HOME_STRAIGHT = 1;

    private TurnConstants() {}
}
