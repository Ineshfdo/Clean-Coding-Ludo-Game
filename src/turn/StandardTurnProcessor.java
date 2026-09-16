package turn;

import java.util.List;

import rule.BetaRestrictionRule;
import rule.BlockadeBreakRule;
import rule.CaptureRule;
import rule.MysteryCellTeleportRule;
import rule.RollValidityRule;
import rule.TurnRule;
import strategy.PlayerStrategyRegistry;

public final class StandardTurnProcessor extends TurnProcessor {

    private static final int BONUS_ROLL_TRIGGER_VALUE = 6;

    public StandardTurnProcessor(
            List<TurnRule> turnRules, PlayerStrategyRegistry strategyRegistry,
            RollValidityRule rollValidityRule, CaptureRule captureRule,
            BlockadeBreakRule blockadeBreakRule, MysteryCellTeleportRule mysteryCellTeleportRule,
            BetaRestrictionRule betaRestrictionRule) {
        super(turnRules, strategyRegistry, rollValidityRule, captureRule, blockadeBreakRule,
                mysteryCellTeleportRule, betaRestrictionRule);
    }

    @Override
    protected boolean grantsAnotherRoll(int rollValue, boolean capturedOpponent) {
        return rollValue == BONUS_ROLL_TRIGGER_VALUE || capturedOpponent;
    }
}
