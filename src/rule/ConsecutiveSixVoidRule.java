package rule;

// Rule 4: a third CONSECUTIVE six is voided - the roll produces no move. Driven by a
// dedicated consecutive-six count (see TurnProcessor), not the turn's overall roll
// number, so a T-2 capture bonus roll can never masquerade as part of the six streak.
public final class ConsecutiveSixVoidRule extends RollValidityRule {

    private static final int VOIDING_CONSECUTIVE_SIX_COUNT = 3;
    private static final int VOIDING_ROLL_VALUE = 6;

    @Override
    protected boolean appliesTo(int consecutiveSixCount, int rollValue) {
        return consecutiveSixCount == VOIDING_CONSECUTIVE_SIX_COUNT && rollValue == VOIDING_ROLL_VALUE;
    }
}
