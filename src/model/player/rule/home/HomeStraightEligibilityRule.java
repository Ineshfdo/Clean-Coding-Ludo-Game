package model.player.rule.home;

import model.piece.Piece;

// T-7: entering HomeStraight requires having captured an opponent.
public final class HomeStraightEligibilityRule extends HomeStraightEntryRule {

    private static final int REQUIRED_CAPTURE_COUNT = 1;

    @Override
    protected boolean appliesTo(Piece piece) {
        return piece.getCaptureCount() < REQUIRED_CAPTURE_COUNT;
    }
}
