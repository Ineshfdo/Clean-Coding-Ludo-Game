package model.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import config.enums.PlayerColor;
import model.board.Board;
import model.board.LudoBoard;
import model.direction.ClockwiseMovementStrategy;
import model.direction.CounterClockwiseMovementStrategy;
import model.direction.MovementDirectionStrategy;
import model.piece.Piece;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.Fixtures;

// Red's Approach is cell 26 and its HomeStraight has five cells (indexes 0-4).
@DisplayName("PieceMovement")
class PieceMovementTest {

    private static final MovementDirectionStrategy CLOCKWISE = ClockwiseMovementStrategy.getInstance();
    private static final MovementDirectionStrategy COUNTER_CLOCKWISE = CounterClockwiseMovementStrategy.getInstance();
    private static final HomeEntryPolicy ENTRY_ALLOWED = piece -> false;
    private static final HomeEntryPolicy ENTRY_FORBIDDEN = piece -> true;

    private final Board board = LudoBoard.getInstance();

    private void move(Piece piece, int steps, HomeEntryPolicy policy, MovementDirectionStrategy direction) {
        PieceMovement.move(piece, steps, board, policy, direction);
    }

    @Nested
    @DisplayName("on the track")
    class OnTheTrack {

        @Test
        void clockwiseMoveAdvancesTheTrackPosition() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 10);

            move(piece, 5, ENTRY_ALLOWED, CLOCKWISE);

            assertEquals(15, piece.getTrackPosition());
        }

        @Test
        void clockwiseMoveWrapsAroundTheEndOfTheTrack() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 50);

            move(piece, 5, ENTRY_ALLOWED, CLOCKWISE);

            assertEquals(3, piece.getTrackPosition());
        }

        @Test
        void counterClockwiseMoveGoesTowardsLowerCells() {
            Piece piece = Fixtures.pieceOnTrack(PlayerColor.RED, 1, 40, COUNTER_CLOCKWISE);

            move(piece, 5, ENTRY_ALLOWED, COUNTER_CLOCKWISE);

            assertEquals(35, piece.getTrackPosition());
        }

        @Test
        void aMoveShortOfApproachDoesNotCountAsAPass() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 10);

            move(piece, 5, ENTRY_ALLOWED, CLOCKWISE);

            assertEquals(0, piece.getApproachPassCount());
        }

        @Test
        void landingExactlyOnApproachStaysOnTheTrack() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 20);

            move(piece, 6, ENTRY_ALLOWED, CLOCKWISE);

            assertTrue(piece.isOnTrack());
            assertEquals(26, piece.getTrackPosition());
        }

        @Test
        void landingExactlyOnApproachCountsAsAPass() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 20);

            move(piece, 6, ENTRY_ALLOWED, CLOCKWISE);

            assertEquals(1, piece.getApproachPassCount());
        }

        @Test
        void counterClockwiseLandingOnApproachAlsoCountsAsAPass() {
            Piece piece = Fixtures.pieceOnTrack(PlayerColor.RED, 1, 30, COUNTER_CLOCKWISE);

            move(piece, 4, ENTRY_ALLOWED, COUNTER_CLOCKWISE);

            assertEquals(26, piece.getTrackPosition());
            assertEquals(1, piece.getApproachPassCount());
        }
    }

    @Nested
    @DisplayName("passing Approach")
    class PassingApproach {

        @Test
        void entersTheHomeStraightWhenEntryIsAllowed() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 24);

            // 2 steps reach Approach, the remaining 3 walk into the HomeStraight (index 2).
            move(piece, 5, ENTRY_ALLOWED, CLOCKWISE);

            assertTrue(piece.isOnHomeStraight());
            assertEquals(2, piece.getHomeStraightIndex());
        }

        @Test
        void passingApproachCountsAsAPass() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 24);

            move(piece, 5, ENTRY_ALLOWED, CLOCKWISE);

            assertEquals(1, piece.getApproachPassCount());
        }

        @Test
        void continuesAlongTheTrackWhenEntryIsForbidden() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 24);

            move(piece, 5, ENTRY_FORBIDDEN, CLOCKWISE);

            assertTrue(piece.isOnTrack());
            assertEquals(29, piece.getTrackPosition());
        }

        @Test
        void aForbiddenEntryStillCountsThePass() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 24);

            move(piece, 5, ENTRY_FORBIDDEN, CLOCKWISE);

            assertEquals(1, piece.getApproachPassCount());
        }

        @Test
        void aPieceStandingOnApproachEntersWithoutAnotherPass() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 26);

            move(piece, 3, ENTRY_ALLOWED, CLOCKWISE);

            assertEquals(2, piece.getHomeStraightIndex());
            assertEquals(0, piece.getApproachPassCount());
        }

        @Test
        void aLongMoveFromApproachCanGoStraightHome() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 26);

            move(piece, 6, ENTRY_ALLOWED, CLOCKWISE);

            assertTrue(piece.isHome());
        }
    }

    @Nested
    @DisplayName("on the HomeStraight")
    class OnTheHomeStraight {

        private Piece pieceAtIndex(int index) {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 26);
            piece.moveToHomeStraight(index);
            return piece;
        }

        @Test
        void movesAlongTheHomeStraight() {
            Piece piece = pieceAtIndex(1);

            move(piece, 2, ENTRY_ALLOWED, CLOCKWISE);

            assertEquals(3, piece.getHomeStraightIndex());
        }

        @Test
        void landingExactlyOnHomeFinishesThePiece() {
            Piece piece = pieceAtIndex(2);

            move(piece, 3, ENTRY_ALLOWED, CLOCKWISE);

            assertTrue(piece.isHome());
        }

        @Test
        void theLastCellNeedsOneStepToGoHome() {
            Piece piece = pieceAtIndex(4);

            move(piece, 1, ENTRY_ALLOWED, CLOCKWISE);

            assertTrue(piece.isHome());
        }

        @Test
        void overshootingHomeStillEndsAtHome() {
            Piece piece = pieceAtIndex(3);

            move(piece, 4, ENTRY_ALLOWED, CLOCKWISE);

            assertTrue(piece.isHome());
        }

        @Test
        void theEntryPolicyDoesNotApplyOnceOnTheHomeStraight() {
            Piece piece = pieceAtIndex(0);

            move(piece, 2, ENTRY_FORBIDDEN, CLOCKWISE);

            assertEquals(2, piece.getHomeStraightIndex());
        }
    }
}
