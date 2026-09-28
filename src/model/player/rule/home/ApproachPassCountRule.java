package model.player.rule.home;

import model.piece.Piece;

/**
 A piece may enter its HomeStraight only after it has passed its Approach cell often enough: once if it is clockwise and twice if it is counter-clockwise (T-1, T-5).
 */
public final class ApproachPassCountRule extends HomeStraightEntryRule {

    @Override
    protected boolean appliesTo(Piece piece) {
        int requiredPasses =
                piece.getOriginalMovementDirection().getRequiredApproachPassCount();

        return piece.getApproachPassCount() < requiredPasses;
    }
}
