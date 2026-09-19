package model.player.rule;
import model.piece.Piece;

import config.constant.BoardConstants;

// Rule 10: a HomeStraight move must land exactly on Home, not past it.
public final class OvershootHomeRule extends ExactHomeRule {

    @Override
    protected boolean appliesTo(Piece piece, int steps) {
        int newIndex = piece.getHomeStraightIndex() + steps;
        return newIndex > BoardConstants.CELLS_PER_HOME_STRAIGHT;
    }
}
