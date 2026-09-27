package config.constant;


/**
 * Constants of the movement effects and of the Beta restriction.
 */
public final class EffectConstants {

    /**
     * The number of rounds that an Energized or Sick effect and the Beta restriction last (T-12,
     * T-13).
     */
    public static final int EFFECT_DURATION_IN_ROUNDS = 4;

    /**
     * The factor by which Energized multiplies the steps of a move.
     */
    public static final int ENERGIZED_STEP_MULTIPLIER = 2;
    /**
     * The number by which Sick divides the steps of a move.
     */
    public static final int SICK_STEP_DIVISOR = 2;

    /**
     * The roll that counts toward sending a Beta-restricted piece back to Base (T-13).
     */
    public static final int BETA_TRIGGER_ROLL_VALUE = 3;
    /**
     * The number of trigger rolls in a row that send a Beta-restricted piece back to Base (T-13).
     */
    public static final int REQUIRED_CONSECUTIVE_TRIGGER_ROLLS = 2;

    private EffectConstants() {}
}
