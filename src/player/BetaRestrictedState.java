package player;

// T-13: a piece/block teleported to Mystery Cell's Beta cannot move for its remaining rounds,
// and tracks whether its owner has now rolled a 3 in two consecutive rounds.
public final class BetaRestrictedState implements PieceRestrictionState {

    private static final int RESTRICTION_DURATION_IN_ROUNDS = 4;
    private static final int TRIGGER_ROLL_VALUE = 3;
    private static final int REQUIRED_CONSECUTIVE_TRIGGER_ROLLS = 2;

    private final int roundsRemaining;
    private final int consecutiveTriggerRollCount;

    public BetaRestrictedState() {
        this(RESTRICTION_DURATION_IN_ROUNDS, 0);
    }

    private BetaRestrictedState(int roundsRemaining, int consecutiveTriggerRollCount) {
        this.roundsRemaining = roundsRemaining;
        this.consecutiveTriggerRollCount = consecutiveTriggerRollCount;
    }

    @Override
    public boolean forbidsMovement() {
        return true;
    }

    @Override
    public int getRoundsRemaining() {
        return roundsRemaining;
    }

    @Override
    public PieceRestrictionState afterRoundElapses() {
        if (roundsRemaining <= 1) {
            return NoRestrictionState.getInstance();
        }
        return new BetaRestrictedState(roundsRemaining - 1, consecutiveTriggerRollCount);
    }

    @Override
    public PieceRestrictionState afterRollRecorded(int rollValue) {
        int updatedCount = rollValue == TRIGGER_ROLL_VALUE ? consecutiveTriggerRollCount + 1 : 0;
        return new BetaRestrictedState(roundsRemaining, updatedCount);
    }

    @Override
    public boolean hasTriggeredReturnToBase() {
        return consecutiveTriggerRollCount >= REQUIRED_CONSECUTIVE_TRIGGER_ROLLS;
    }
}
