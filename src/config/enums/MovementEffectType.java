package config.enums;

/**
 * The kind of a movement effect (T-12).
 */
public enum MovementEffectType {
    /**
     * No effect: the steps of a move do not change.
     */
    NONE,
    /**
     * Energized: the steps of a move are doubled.
     */
    ENERGIZED,
    /**
     * Sick: the steps of a move are halved.
     */
    SICK
}
