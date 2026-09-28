package config.constant;


public final class TurnConstants {

  
    // The Number "1", used to mark a turn's first roll.
    public static final int FIRST_ROLL_OF_TURN = 1;

    // Number of consecutive rolls, once all opponents are Home, before the home gate opens
    public static final int CONSECUTIVE_ROLLS_TO_OPEN_HOME_GATE = 3;

    public static final int REQUIRED_CAPTURES_TO_ENTER_HOME_STRAIGHT = 1;

    private TurnConstants() {}
}
