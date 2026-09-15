package turn;

import java.util.List;

import rule.BlockadeBreakRule;
import rule.CaptureRule;
import rule.RollValidityRule;
import rule.TurnRule;
import strategy.PlayerStrategy;

public final class StandardTurnProcessor extends TurnProcessor {

    private static final int BONUS_ROLL_TRIGGER_VALUE = 6;

    public StandardTurnProcessor(
            List<TurnRule> turnRules, PlayerStrategy strategy,
            RollValidityRule rollValidityRule, CaptureRule captureRule,
            BlockadeBreakRule blockadeBreakRule) {
        super(turnRules, strategy, rollValidityRule, captureRule, blockadeBreakRule);
    }

    @Override
    protected boolean grantsAnotherRoll(int rollValue, boolean capturedOpponent) {
        return rollValue == BONUS_ROLL_TRIGGER_VALUE || capturedOpponent;
    }
}
