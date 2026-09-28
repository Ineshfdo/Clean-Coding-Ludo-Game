package model.effect.restriction;

import config.constant.EffectConstants;

/**
 The state of a piece that was teleported to Beta (T-13).
 The piece cannot move for four rounds, and two rolls of 3 in a row send it back to Base.
 */
public final class BetaRestrictedState implements PieceRestrictionState {

    private final int roundsRemaining;
    private final int consecutiveTriggerRollCount;

    /**
     Creates a new Beta restriction with all its rounds left and no rolls of 3 counted.
     */
    public BetaRestrictedState() {
        this(EffectConstants.EFFECT_DURATION_IN_ROUNDS, 0);
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

    // T-13: lasts all 4 rounds after the landing round.
    @Override
    public PieceRestrictionState afterRoundElapses() {
        if (roundsRemaining <= 0) {
            return NoRestrictionState.getInstance();
        }

        return new BetaRestrictedState(roundsRemaining - 1, consecutiveTriggerRollCount);
    }

    @Override
    public PieceRestrictionState afterRollRecorded(int rollValue) {
        int updatedCount =
                rollValue == EffectConstants.BETA_TRIGGER_ROLL_VALUE ? consecutiveTriggerRollCount + 1 : 0;

        return new BetaRestrictedState(roundsRemaining, updatedCount);
    }

    @Override
    public boolean hasTriggeredReturnToBase() {
        return consecutiveTriggerRollCount >= EffectConstants.REQUIRED_CONSECUTIVE_TRIGGER_ROLLS;
    }
}
