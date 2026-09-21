package model.player;

import config.constant.BoardConstants;
import model.board.Board;
import model.direction.MovementDirectionStrategy;
import model.piece.Piece;

// Rule 1/T-1: moves a piece along the track or the HomeStraight by a number of steps.
final class PieceMovement {

    private PieceMovement() {
    }

    static void move(
            Piece piece, int steps, Board board, HomeEntryPolicy homeEntryPolicy,
            MovementDirectionStrategy travelDirection) {
        if (piece.isOnHomeStraight()) {
            moveAlongHomeStraight(piece, steps, board);
        } else {
            moveAlongTrack(piece, steps, board, homeEntryPolicy, travelDirection);
        }
    }

    // T-1: reaching Approach enters HomeStraight only if the entry policy allows.
    private static void moveAlongTrack(
            Piece piece, int steps, Board board, HomeEntryPolicy homeEntryPolicy,
            MovementDirectionStrategy travelDirection) {
        int stepsToApproach =
                travelDirection.countStepsToApproach(piece.getTrackPosition(), piece.getColor(), board);

        if (steps < stepsToApproach) {
            piece.moveTo(travelDirection.nextPosition(piece.getTrackPosition(), steps, board));
            return;
        }

        if (steps == stepsToApproach) {
            // Landing exactly on Approach keeps the piece on the track.
            piece.moveTo(travelDirection.nextPosition(piece.getTrackPosition(), steps, board));
            piece.recordApproachPass();
            return;
        }

        // Leaving Approach isn't a new pass: arriving on it was already counted.
        if (stepsToApproach > 0) {
            piece.recordApproachPass();
        }

        if (homeEntryPolicy.forbidsEntry(piece)) {
            piece.moveTo(travelDirection.nextPosition(piece.getTrackPosition(), steps, board));
            return;
        }

        moveAlongHomeStraight(piece, steps - stepsToApproach, board);
    }

    // Reaching or passing the last HomeStraight cell sends the piece Home.
    private static void moveAlongHomeStraight(Piece piece, int steps, Board board) {
        int currentIndex = piece.isOnHomeStraight() ? piece.getHomeStraightIndex()
                : BoardConstants.BEFORE_FIRST_HOME_STRAIGHT_CELL;
        int newIndex = currentIndex + steps;

        if (newIndex >= board.getHomeStraightLength()) {
            piece.moveHome();
            return;
        }

        piece.moveToHomeStraight(newIndex);
    }
}
