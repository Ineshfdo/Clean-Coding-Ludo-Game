package model.effect.movement;

import config.constant.EffectConstants;
import config.enums.MovementEffectType;

/**
 A movement effect, Energized or Sick, together with the number of rounds it has left (T-12).
 Energized doubles the steps of a move and Sick halves them.
 A piece without an effect holds the none effect.
 */
public final class MovementEffect {

    private static final MovementEffect NONE = new MovementEffect(MovementEffectType.NONE, 0);

    private final MovementEffectType type;
    private final int roundsRemaining;

    private MovementEffect(MovementEffectType type, int roundsRemaining) {
        this.type = type;
        this.roundsRemaining = roundsRemaining;
    }

    /**
     Gives the effect that changes nothing.
     @return the shared none effect
     */
    public static MovementEffect none() {
        return NONE;
    }

    /**
     Creates an effect.
     @param type Energized or Sick
     @param durationInRounds the number of rounds the effect lasts
     @return the new effect
     */
    public static MovementEffect of(MovementEffectType type, int durationInRounds) {
        return new MovementEffect(type, durationInRounds);
    }

    /**
     Changes the steps of a move by this effect.
     @param steps the steps before the effect
     @return the steps after the effect: doubled, halved and rounded down, or unchanged
     */
    public int applyTo(int steps) {
        return switch (type) {
            case ENERGIZED -> steps * EffectConstants.ENERGIZED_STEP_MULTIPLIER;
            case SICK -> steps / EffectConstants.SICK_STEP_DIVISOR;
            case NONE -> steps;
        };
    }

    /**
     Gives the text shown on the console.
     @return Energized, Sick or None
     */
    public String getLabel() {
        return switch (type) {
            case ENERGIZED -> "Energized";
            case SICK -> "Sick";
            case NONE -> "None";
        };
    }

    /**
     Tells whether the effect is Energized or Sick.
     This depends on the type, not on the rounds left.
     @return false only for the none effect
     */
    public boolean isActive() {
        return type != MovementEffectType.NONE;
    }

    /**
     Uses up one round (T-12).
     The effect expires lazily, so it lasts four full rounds.
     @return the effect after one round; the none effect when it is used up
     */
    public MovementEffect afterRoundElapses() {
        if (roundsRemaining <= 0) {
            return NONE;
        }

        return new MovementEffect(type, roundsRemaining - 1);
    }
}
