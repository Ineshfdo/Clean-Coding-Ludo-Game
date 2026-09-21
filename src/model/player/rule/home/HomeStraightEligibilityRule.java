package model.player.rule.home;

import config.constant.TurnConstants;
import model.piece.Piece;

// T-7: entering HomeStraight requires a capture, unless the home gate is open for the piece's color.
public final class HomeStraightEligibilityRule extends HomeStraightEntryRule {

    private final HomeGateStatus homeGate;

    public HomeStraightEligibilityRule(HomeGateStatus homeGate) {
        this.homeGate = homeGate;
    }

    @Override
    protected boolean appliesTo(Piece piece) {
        boolean lacksRequiredCapture = piece.getCaptureCount() < TurnConstants.REQUIRED_CAPTURES_TO_ENTER_HOME_STRAIGHT;

        return lacksRequiredCapture && !homeGate.isOpenFor(piece.getColor());
    }
}
