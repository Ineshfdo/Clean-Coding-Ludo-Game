package config.constant;

public final class TurnConstants {

    // Marks the first dice roll of a turn, separate from any bonus rolls that follow.
    
    public static final int FIRST_ROLL_OF_TURN = 1;

    // Rolls in a row, with every opponent already Home, before the home gate opens.
    public static final int CONSECUTIVE_ROLLS_TO_OPEN_HOME_GATE = 3;

    // Opponent pieces a piece must capture before it may enter its HomeStraight.
    public static final int REQUIRED_CAPTURES_TO_ENTER_HOME_STRAIGHT = 1;

    private TurnConstants() {}
}
