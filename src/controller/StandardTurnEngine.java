package controller;

import config.constant.DiceConstants;
import java.util.List;
import model.player.rule.BetaRestrictionRule;
import model.player.rule.BlockadeBreakRule;
import model.player.rule.CaptureRule;
import model.player.rule.MysteryCellTeleportRule;
import model.player.rule.RollValidityRule;
import model.player.rule.TurnRule;
import model.player.strategy.PlayerStrategyRegistry;

public final class StandardTurnEngine extends TurnEngine {

    public StandardTurnEngine(
        List<TurnRule> turnRules, PlayerStrategyRegistry strategyRegistry,
        RollValidityRule rollValidityRule, CaptureRule captureRule,
        BlockadeBreakRule blockadeBreakRule, MysteryCellTeleportRule mysteryCellTeleportRule,
        BetaRestrictionRule betaRestrictionRule) {
        super(turnRules, strategyRegistry, rollValidityRule, captureRule, blockadeBreakRule,
            mysteryCellTeleportRule, betaRestrictionRule);
    }

    @Override
    protected boolean grantsAnotherRoll(int rollValue, boolean capturedOpponent) {
        return rollValue == DiceConstants.SIX_ROLL_VALUE || capturedOpponent;
    }
}
