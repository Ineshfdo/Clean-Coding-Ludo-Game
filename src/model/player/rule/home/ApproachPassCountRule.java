package model.player.rule.home;

import model.piece.Piece;

// T-1/T-5: Approach passes needed: 2 counter-clockwise, 1 clockwise.
public final class ApproachPassCountRule extends HomeStraightEntryRule {

    @Override
    protected boolean appliesTo(Piece piece) {
        int requiredPasses =
                piece.getOriginalMovementDirectionStrategy().getRequiredApproachPassCount();

        return piece.getApproachPassCount() < requiredPasses;
    }
}
