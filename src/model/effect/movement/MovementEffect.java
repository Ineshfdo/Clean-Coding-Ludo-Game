package model.effect.movement;

import config.enums.MovementEffectType;

// T-12: an effect type plus the rounds it has left.
public final class MovementEffect {

    private static final MovementEffect NONE = new MovementEffect(MovementEffectType.NONE, 0);

    private final MovementEffectType type;
    private final int roundsRemaining;

    private MovementEffect(MovementEffectType type, int roundsRemaining) {
        this.type = type;
        this.roundsRemaining = roundsRemaining;
    }

    public static MovementEffect none() {
        return NONE;
    }

    public static MovementEffect of(MovementEffectType type, int durationInRounds) {
        return new MovementEffect(type, durationInRounds);
    }

    public int applyTo(int steps) {
        return switch (type) {
            case ENERGIZED -> steps * 2;
            case SICK -> steps / 2;
            case NONE -> steps;
        };
    }

    public String getLabel() {
        return switch (type) {
            case ENERGIZED -> "Energized";
            case SICK -> "Sick";
            case NONE -> "None";
        };
    }

    // T-12: active by type, not by the round counter.
    public boolean isActive() {
        return type != MovementEffectType.NONE;
    }

    // T-12: expires lazily, so the effect lasts four full rounds.
    public MovementEffect afterRoundElapses() {
        if (roundsRemaining <= 0) {
            return NONE;
        }

        return new MovementEffect(type, roundsRemaining - 1);
    }
}
