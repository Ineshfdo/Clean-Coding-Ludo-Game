package model.player.action;

import config.constant.BoardConstants;
import model.position.MovementDirectionStrategy;
import model.board.Board;
import model.player.rule.HomeStraightEntryRule;
import model.piece.Piece;

// T-17: shared by MoveCommand/BlockMoveCommand to preview whether a move sends the piece
// Home, without executing it - used by GreenStrategy's "move pieces Home first" priority.
final class HomeArrivalPreview {

    private HomeArrivalPreview() {
    }

    static boolean resolve(
            Piece piece, int steps, Board board, HomeStraightEntryRule homeStraightEntryRule,
            MovementDirectionStrategy travelDirection) {
        if (piece.isOnHomeStraight()) {
            // MovementRule already validated this against ExactHomeRule before this command
            // was ever constructed, so reaching the last index here is always legitimate.
            return piece.getHomeStraightIndex() + steps >= BoardConstants.CELLS_PER_HOME_STRAIGHT;
        }
        if (!piece.isOnTrack()) {
            return false;
        }

        int stepsToApproach = travelDirection.stepsToApproach(piece.getTrackPosition(), piece.getColor(), board);
        if (steps <= stepsToApproach) {
            return false;
        }
        // T-1/T-5: forbidsEntry reads the CURRENT approach-pass count, while the real move
        // increments it first - so a piece on its qualifying pass may preview as "not yet
        // eligible" here. A rare, documented simplification: it just falls through to
        // GreenStrategy's next priority instead of picking this move a moment too early.
        if (homeStraightEntryRule.forbidsEntry(piece)) {
            return false;
        }

        int homeStraightSteps = steps - stepsToApproach;
        return homeStraightSteps - 1 >= BoardConstants.CELLS_PER_HOME_STRAIGHT;
    }
}
