package turn;

import java.util.List;

import rule.RollValidityRule;
import rule.TurnRule;
import strategy.PlayerStrategy;

public final class StandardTurnProcessor extends TurnProcessor {

    private static final int BONUS_ROLL_TRIGGER_VALUE = 6;

    public StandardTurnProcessor(
            List<TurnRule> turnRules, PlayerStrategy strategy, RollValidityRule rollValidityRule) {
        super(turnRules, strategy, rollValidityRule);
    }

    @Override
    protected boolean grantsAnotherRoll(int rollValue) {
        return rollValue == BONUS_ROLL_TRIGGER_VALUE;
    }
}
