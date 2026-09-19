package config.constant;

public final class TurnConstants {

    // Marks the first dice roll of a turn, separate from any bonus rolls that follow.
    
    public static final int FIRST_ROLL_OF_TURN = 1;

    // Rolls in a row, with every opponent already Home, before the home gate opens.
    public static final int CONSECUTIVE_ROLLS_TO_OPEN_HOME_GATE = 3;

    private TurnConstants() {}
}
