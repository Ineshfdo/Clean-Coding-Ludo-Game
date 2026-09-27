package model.effect.restriction;

/**
 * The free state: the piece may move (T-13). It is also the Null Object that every piece holds when
 * nothing restricts it, so the code never has to check for null. The class is a Singleton.
 */
public final class NoRestrictionState implements PieceRestrictionState {

    private static final NoRestrictionState SHARED_INSTANCE = new NoRestrictionState();

    private NoRestrictionState() {
    }

    /**
     * Gives the one shared free state.
     *
     * @return the shared instance
     */
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
