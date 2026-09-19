package model.player.command.move;

import config.constant.BoardConstants;
import model.board.Board;
import model.piece.Piece;
import model.player.rule.HomeStraightEntryRule;
import model.position.MovementDirectionStrategy;

// T-17: previews whether a move reaches Home (used by GreenStrategy).
final class HomeArrivalChecker {

    private HomeArrivalChecker() {
    }

    static boolean resolve(
            Piece piece, int steps, Board board, HomeStraightEntryRule homeStraightEntryRule,
            MovementDirectionStrategy travelDirection) {
        if (piece.isOnHomeStraight()) {
            // Already validated by ExactHomeRule, so reaching the last index is legitimate.
            return piece.getHomeStraightIndex() + steps >= BoardConstants.CELLS_PER_HOME_STRAIGHT;
        }

        if (!piece.isOnTrack()) {
            return false;
        }

        int stepsToApproach = travelDirection.stepsToApproach(piece.getTrackPosition(), piece.getColor(), board);

        if (steps <= stepsToApproach) {
            return false;
        }

        // T-1/T-5: the pass count isn't incremented yet, so this may under-report (known simplification).
        if (homeStraightEntryRule.forbidsEntry(piece)) {
            return false;
        }

        int homeStraightSteps = steps - stepsToApproach;

        return homeStraightSteps - 1 >= BoardConstants.CELLS_PER_HOME_STRAIGHT;
    }
}
