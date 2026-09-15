package player;

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
        return type.applyTo(steps);
    }

    public String getLabel() {
        return type.getLabel();
    }

    public boolean isActive() {
        return roundsRemaining > 0;
    }

    // T-12: called once per round; reverts to None once its rounds run out.
    public MovementEffect afterRoundElapses() {
        if (roundsRemaining <= 1) {
            return NONE;
        }
        return new MovementEffect(type, roundsRemaining - 1);
    }
}
