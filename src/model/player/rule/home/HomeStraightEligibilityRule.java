package model.player.rule.home;

import config.constant.TurnConstants;
import model.piece.Piece;

/**
 * A piece needs a capture before it may enter its HomeStraight, unless the home gate is open for
 * its colour (T-7).
 */
public final class HomeStraightEligibilityRule extends HomeStraightEntryRule {

    private final HomeGateStatus homeGate;

    /**
     * Creates the rule.
     *
     * @param homeGate tells whether the home gate is open for a colour
     */
    public HomeStraightEligibilityRule(HomeGateStatus homeGate) {
        this.homeGate = homeGate;
    }

    @Override
    protected boolean appliesTo(Piece piece) {
        boolean lacksRequiredCapture = piece.getCaptureCount() < TurnConstants.REQUIRED_CAPTURES_TO_ENTER_HOME_STRAIGHT;

        return lacksRequiredCapture && !homeGate.isOpenFor(piece.getColor());
    }
}
