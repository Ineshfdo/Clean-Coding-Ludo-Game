package model.player.command.move;

import model.board.Board;
import model.direction.MovementDirectionStrategy;
import model.piece.Piece;
import model.player.HomeEntryPolicy;

// T-17: previews whether a move reaches Home (used by GreenStrategy).
final class HomeArrivalChecker {

    private HomeArrivalChecker() {
    }

    static boolean reachesHome(
            Piece piece, int steps, Board board, HomeEntryPolicy homeEntryPolicy,
            MovementDirectionStrategy travelDirection) {
        if (piece.isOnHomeStraight()) {
            // Already validated by ExactRollRule, so reaching the last index is legitimate.
            return piece.getHomeStraightIndex() + steps >= board.getHomeStraightLength();
        }

        if (!piece.isOnTrack()) {
            return false;
        }

        int stepsToApproach = travelDirection.countStepsToApproach(piece.getTrackPosition(), piece.getColor(), board);

        if (steps <= stepsToApproach) {
            return false;
        }

        // T-1/T-5: the pass count isn't incremented yet, so this may under-report (known simplification).
        if (homeEntryPolicy.forbidsEntry(piece)) {
            return false;
        }

        int homeStraightSteps = steps - stepsToApproach;

        return homeStraightSteps - 1 >= board.getHomeStraightLength();
    }

    // Red: previews whether a move carries a track piece into HomeStraight or Home.
    static boolean leavesTrack(
            Piece piece, int steps, Board board, HomeEntryPolicy homeEntryPolicy,
            MovementDirectionStrategy travelDirection) {
        if (!piece.isOnTrack()) {
            return false;
        }

        int stepsToApproach = travelDirection.countStepsToApproach(piece.getTrackPosition(), piece.getColor(), board);

        if (steps <= stepsToApproach) {
            return false;
        }

        // Crossing Approach is counted before the entry check, so check a copy that already has it.
        int crossingsCounted = stepsToApproach > 0 ? 1 : 0;

        return !homeEntryPolicy.forbidsEntry(piece.copyForPreview(crossingsCounted));
    }
}
