package model.player.rule.home;

import model.piece.Piece;

// T-7: entering HomeStraight requires a capture, unless the home gate is open for the piece's color.
public final class HomeStraightEligibilityRule extends HomeStraightEntryRule {

    private static final int REQUIRED_CAPTURE_COUNT = 1;

    private final HomeGateStatus homeGate;

    public HomeStraightEligibilityRule(HomeGateStatus homeGate) {
        this.homeGate = homeGate;
    }

    @Override
    protected boolean appliesTo(Piece piece) {
        boolean lacksRequiredCapture = piece.getCaptureCount() < REQUIRED_CAPTURE_COUNT;

        return lacksRequiredCapture && !homeGate.isOpenFor(piece.getColor());
    }
}
