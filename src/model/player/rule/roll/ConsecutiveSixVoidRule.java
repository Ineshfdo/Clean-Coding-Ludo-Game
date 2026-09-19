package model.player.rule.roll;

import config.constant.DiceConstants;

// Rule 4: a third consecutive six is voided; no move.
public final class ConsecutiveSixVoidRule extends RollValidityRule {

    @Override
    protected boolean appliesTo(int consecutiveSixCount, int rollValue) {
        return consecutiveSixCount == DiceConstants.THIRD_CONSECUTIVE_SIX_COUNT
            && rollValue == DiceConstants.SIX_ROLL_VALUE;
    }
}
