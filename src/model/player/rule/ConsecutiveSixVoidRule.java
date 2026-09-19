package model.player.rule;

import config.constant.DiceConstants;

// Rule 4: a third CONSECUTIVE six is voided - the roll produces no move. Driven by a
// dedicated consecutive-six count (see GameEngine), not the turn's overall roll
// number, so a T-2 capture bonus roll can never masquerade as part of the six streak.
public final class ConsecutiveSixVoidRule extends RollValidityRule {

    @Override
    protected boolean appliesTo(int consecutiveSixCount, int rollValue) {
        return consecutiveSixCount == DiceConstants.THIRD_CONSECUTIVE_SIX_COUNT
                && rollValue == DiceConstants.SIX_ROLL_VALUE;
    }
}
