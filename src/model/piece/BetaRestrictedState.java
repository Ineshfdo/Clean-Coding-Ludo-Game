package model.piece;

import config.constant.EffectConstants;

// T-13: a piece/block teleported to Mystery Cell's Beta cannot move for its remaining rounds,
// and tracks whether its owner has now rolled a 3 in two consecutive rounds.
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

    // T-13: the landing round already spent its move reaching Beta, so all 4 blocked rounds
    // must fall on the rounds AFTER creation - expiry only fires once nothing is left to consume.
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
