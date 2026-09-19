package model.piece;

// T-13: the default state - every piece starts free to move, until a rule (such as landing
// on Mystery Cell's Beta) places it into BetaRestrictedState.
public final class NoRestrictionState implements PieceRestrictionState {

    private static final NoRestrictionState INSTANCE = new NoRestrictionState();

    private NoRestrictionState() {
    }

    public static NoRestrictionState getInstance() {
        return INSTANCE;
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
