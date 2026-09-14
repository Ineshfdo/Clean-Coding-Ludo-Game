package player;

// T-1: counter-clockwise needs 2 Approach passes to enter HomeStraight; clockwise needs only 1.
public final class ApproachPassCountRule extends HomeStraightEntryRule {

    @Override
    protected boolean appliesTo(Piece piece) {
        int requiredPasses = piece.getMovementDirectionStrategy().getRequiredApproachPassCount();
        return piece.getApproachPassCount() < requiredPasses;
    }
}
