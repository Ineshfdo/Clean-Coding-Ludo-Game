package player;

import ludoboard.HomeStraightCell;

// Rule 10: a HomeStraight move must land exactly on Home, not past it.
public final class OvershootHomeRule extends ExactHomeRule {

    @Override
    protected boolean appliesTo(Piece piece, int steps) {
        int newIndex = piece.getHomeStraightIndex() + steps;
        return newIndex > HomeStraightCell.CELLS_PER_HOME_STRAIGHT;
    }
}
