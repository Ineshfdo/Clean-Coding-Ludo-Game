package model.player.command.move;

import config.constant.BoardConstants;
import model.board.Board;
import model.direction.MovementDirectionStrategy;
import model.piece.Piece;
import model.player.rule.home.HomeStraightEntryRule;

// T-17: previews whether a move reaches Home (used by GreenStrategy).
final class HomeArrivalChecker {

    private HomeArrivalChecker() {
    }

    static boolean resolve(
            Piece piece, int steps, Board board, HomeStraightEntryRule homeStraightEntryRule,
            MovementDirectionStrategy travelDirection) {
        if (piece.isOnHomeStraight()) {
            // Already validated by ExactRollRule, so reaching the last index is legitimate.
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

    // Red: previews whether a move carries a track piece into HomeStraight or Home.
    static boolean leavesTrack(
            Piece piece, int steps, Board board, HomeStraightEntryRule homeStraightEntryRule,
            MovementDirectionStrategy travelDirection) {
        if (!piece.isOnTrack()) {
            return false;
        }

        int stepsToApproach = travelDirection.stepsToApproach(piece.getTrackPosition(), piece.getColor(), board);

        if (steps <= stepsToApproach) {
            return false;
        }

        // Crossing Approach is counted before the entry check, so check a copy that already has it.
        int crossingsCounted = stepsToApproach > 0 ? 1 : 0;

        return !homeStraightEntryRule.forbidsEntry(copyAfterCrossings(piece, crossingsCounted));
    }

    // Carries only what the entry rules read: color, original direction, passes and captures.
    private static Piece copyAfterCrossings(Piece piece, int crossingsCounted) {
        Piece copy = new Piece(piece.getColor(), 1);

        copy.assignMovementDirection(piece.getOriginalMovementDirectionStrategy());

        for (int pass = 0; pass < piece.getApproachPassCount() + crossingsCounted; pass++) {
            copy.recordApproachPass();
        }

        for (int capture = 0; capture < piece.getCaptureCount(); capture++) {
            copy.recordCapture();
        }

        return copy;
    }
}
