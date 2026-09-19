package model.player.rule.home;

import config.constant.BoardConstants;
import model.piece.Piece;

// Rule 10: a HomeStraight move must land exactly on Home.
public final class OvershootHomeRule extends ExactRollRule {

    @Override
    protected boolean appliesTo(Piece piece, int steps) {
        int newIndex = piece.getHomeStraightIndex() + steps;

        return newIndex > BoardConstants.CELLS_PER_HOME_STRAIGHT;
    }
}
