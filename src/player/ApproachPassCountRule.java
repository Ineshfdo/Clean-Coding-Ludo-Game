package player;

// T-1/T-5: counter-clockwise needs 2 Approach passes; clockwise needs only 1 - by own direction.
public final class ApproachPassCountRule extends HomeStraightEntryRule {

    @Override
    protected boolean appliesTo(Piece piece) {
        int requiredPasses =
                piece.getOriginalMovementDirectionStrategy().getRequiredApproachPassCount();
        return piece.getApproachPassCount() < requiredPasses;
    }
}
