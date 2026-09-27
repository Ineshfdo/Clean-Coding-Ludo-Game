package model.player.rule.roll;

import config.constant.DiceConstants;

/**
 * A third six in a row is void, and the player makes no move (rule 4).
 */
public final class ConsecutiveSixVoidRule extends RollValidityRule {

    @Override
    protected boolean appliesTo(int consecutiveSixCount, int rollValue) {
        return consecutiveSixCount == DiceConstants.THIRD_CONSECUTIVE_SIX_COUNT
            && rollValue == DiceConstants.SIX_ROLL_VALUE;
    }
}
