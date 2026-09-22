package model.player.command.move;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import config.enums.PlayerColor;
import java.util.Optional;
import model.board.Board;
import model.board.LudoBoard;
import model.direction.ClockwiseMovementStrategy;
import model.direction.CounterClockwiseMovementStrategy;
import model.direction.MovementDirectionStrategy;
import model.piece.Piece;
import model.player.HomeEntryPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.Fixtures;

// Red's Approach is cell 26. These two helpers preview a move without changing the piece.
@DisplayName("Move helpers")
class MoveHelpersTest {

    private static final MovementDirectionStrategy CLOCKWISE = ClockwiseMovementStrategy.getInstance();
    private static final MovementDirectionStrategy COUNTER_CLOCKWISE = CounterClockwiseMovementStrategy.getInstance();
    private static final HomeEntryPolicy ENTRY_ALLOWED = piece -> false;
    private static final HomeEntryPolicy ENTRY_FORBIDDEN = piece -> true;

    private final Board board = LudoBoard.getInstance();

    @Nested
    @DisplayName("TrackLandingFinder")
    class TrackLanding {

        @Test
        void aMoveShortOfApproachHasALandingCell() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 10);

            assertEquals(Optional.of(14), TrackLandingFinder.findLandingPosition(piece, 4, board, CLOCKWISE));
        }

        @Test
        void aMoveLandingExactlyOnApproachStillHasALandingCell() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 20);

            assertEquals(Optional.of(26), TrackLandingFinder.findLandingPosition(piece, 6, board, CLOCKWISE));
        }

        @Test
        void aMovePastApproachHasNoLandingCell() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 20);

            assertTrue(TrackLandingFinder.findLandingPosition(piece, 7, board, CLOCKWISE).isEmpty());
        }

        @Test
        void counterClockwiseLandingGoesTowardsLowerCells() {
            Piece piece = Fixtures.pieceOnTrack(PlayerColor.RED, 1, 40, COUNTER_CLOCKWISE);

            assertEquals(Optional.of(36), TrackLandingFinder.findLandingPosition(piece, 4, board, COUNTER_CLOCKWISE));
        }

        @Test
        void aPieceNotOnTheTrackHasNoLandingCell() {
            Piece piece = new Piece(PlayerColor.RED, 1);

            assertTrue(TrackLandingFinder.findLandingPosition(piece, 4, board, CLOCKWISE).isEmpty());
        }
    }

    @Nested
    @DisplayName("HomeArrivalChecker")
    class HomeArrival {

        @Test
        void aPieceAtBaseNeitherReachesHomeNorLeavesTheTrack() {
            Piece piece = new Piece(PlayerColor.RED, 1);

            assertFalse(HomeArrivalChecker.reachesHome(piece, 6, board, ENTRY_ALLOWED, CLOCKWISE));
            assertFalse(HomeArrivalChecker.leavesTrack(piece, 6, board, ENTRY_ALLOWED, CLOCKWISE));
        }

        @Test
        void aHomeStraightPieceReachesHomeWithExactlyTheRemainingCells() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 26);
            piece.moveToHomeStraight(2);

            assertTrue(HomeArrivalChecker.reachesHome(piece, 3, board, ENTRY_ALLOWED, CLOCKWISE));
            assertFalse(HomeArrivalChecker.reachesHome(piece, 2, board, ENTRY_ALLOWED, CLOCKWISE));
        }

        @Test
        void aTrackMoveThatStopsAtApproachDoesNotReachHome() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 20);

            assertFalse(HomeArrivalChecker.reachesHome(piece, 6, board, ENTRY_ALLOWED, CLOCKWISE));
        }

        @Test
        void aTrackMoveThatMayNotEnterDoesNotReachHome() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 26);

            assertFalse(HomeArrivalChecker.reachesHome(piece, 6, board, ENTRY_FORBIDDEN, CLOCKWISE));
        }

        @Test
        void leavesTrackIsTrueOnceApproachIsCrossedAndEntryIsAllowed() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 24);

            assertTrue(HomeArrivalChecker.leavesTrack(piece, 3, board, ENTRY_ALLOWED, CLOCKWISE));
        }

        @Test
        void leavesTrackIsFalseWhenTheMoveEndsOnOrBeforeApproach() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 24);

            assertFalse(HomeArrivalChecker.leavesTrack(piece, 2, board, ENTRY_ALLOWED, CLOCKWISE));
        }

        @Test
        void leavesTrackChecksEntryAsIfTheCrossingWasAlreadyCounted() {
            // Entry is only allowed once one Approach pass has been recorded on the copy.
            HomeEntryPolicy needsOnePass = piece -> piece.getApproachPassCount() < 1;
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 24);

            assertTrue(HomeArrivalChecker.leavesTrack(piece, 3, board, needsOnePass, CLOCKWISE));
        }

        @Test
        void leavesTrackNeverChangesTheRealPiece() {
            Piece piece = Fixtures.clockwisePieceOnTrack(PlayerColor.RED, 1, 24);

            HomeArrivalChecker.leavesTrack(piece, 3, board, ENTRY_ALLOWED, CLOCKWISE);

            assertEquals(0, piece.getApproachPassCount());
            assertEquals(24, piece.getTrackPosition());
        }
    }
}
