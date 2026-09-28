package config.constant;



public final class EffectConstants {

  
    public static final int EFFECT_DURATION_IN_ROUNDS = 4;


    public static final int ENERGIZED_STEP_MULTIPLIER = 2;
    public static final int SICK_STEP_DIVISOR = 2;

    
    // The roll that counts toward sending a Beta-restricted piece back to Base (T-13).
    public static final int BETA_TRIGGER_ROLL_VALUE = 3;
    
    // The number of trigger rolls in a row that send a Beta-restricted piece back to Base (T-13).
    public static final int REQUIRED_CONSECUTIVE_TRIGGER_ROLLS = 2;

    private EffectConstants() {}
}
