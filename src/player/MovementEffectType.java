package player;

// T-12: State pattern - encapsulates how Energized/Sick modifies movement, so movement
// code never branches on "am I energized?" / "am I sick?" itself.
public enum MovementEffectType {
    NONE,
    ENERGIZED,
    SICK;

    public int applyTo(int steps) {
        return switch (this) {
            case ENERGIZED -> steps * 2;
            case SICK -> steps / 2;
            case NONE -> steps;
        };
    }

    public String getLabel() {
        return switch (this) {
            case ENERGIZED -> "Energized";
            case SICK -> "Sick";
            case NONE -> "None";
        };
    }
}
