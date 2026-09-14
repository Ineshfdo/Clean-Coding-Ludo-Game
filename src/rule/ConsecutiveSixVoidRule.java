package rule;

// Rule 4: a third consecutive six is voided - the roll
// produces no move.
public final class ConsecutiveSixVoidRule extends RollValidityRule {

    private static final int VOIDING_ROLL_NUMBER = 3;
    private static final int VOIDING_ROLL_VALUE = 6;

    @Override
    protected boolean appliesTo(int rollNumber, int rollValue) {
        return rollNumber == VOIDING_ROLL_NUMBER && rollValue == VOIDING_ROLL_VALUE;
    }
}
