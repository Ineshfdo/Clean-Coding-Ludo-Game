package model.effect.restriction;

// T-13: the default state - the piece is free to move.
public final class NoRestrictionState implements PieceRestrictionState {

    private static final NoRestrictionState SHARED_INSTANCE = new NoRestrictionState();

    private NoRestrictionState() {
    }

    public static NoRestrictionState getInstance() {
        return SHARED_INSTANCE;
    }

    @Override
    public boolean forbidsMovement() {
        return false;
    }

    @Override
    public int getRoundsRemaining() {
        return 0;
    }

    @Override
    public PieceRestrictionState afterRoundElapses() {
        return this;
    }

    @Override
    public PieceRestrictionState afterRollRecorded(int rollValue) {
        return this;
    }

    @Override
    public boolean hasTriggeredReturnToBase() {
        return false;
    }
}
