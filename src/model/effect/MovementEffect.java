package model.effect;
import config.enums.MovementEffectType;

// T-12: a MovementEffectType paired with how many rounds it still has left.
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

    // T-12: active by type, not by the counter - matches PieceRestrictionState's use of
    // object identity for its own forbidsMovement() (T-13), so both "next four rounds"
    // effects expire on the same schedule (see afterRoundElapses).
    public boolean isActive() {
        return type != MovementEffectType.NONE;
    }

    // T-12: called once per round. Only converts to None once roundsRemaining was ALREADY at
    // or below zero on entry - the same lazy-transition timing BetaRestrictedState.
    // afterRoundElapses() (T-13) uses - so a piece stays affected for four full rounds after
    // the round it was assigned, not three.
    public MovementEffect afterRoundElapses() {
        if (roundsRemaining <= 0) {
            return NONE;
        }
        return new MovementEffect(type, roundsRemaining - 1);
    }
}
