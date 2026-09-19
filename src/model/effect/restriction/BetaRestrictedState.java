package model.effect.restriction;

import config.constant.EffectConstants;

// T-13: a piece on Beta cannot move; consecutive 3s send it back to Base.
public final class BetaRestrictedState implements PieceRestrictionState {

    private final int roundsRemaining;
    private final int consecutiveTriggerRollCount;

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
