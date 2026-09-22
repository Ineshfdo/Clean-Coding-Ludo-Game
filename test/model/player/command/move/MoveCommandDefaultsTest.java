package model.player.command.move;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import config.enums.PlayerColor;
import java.util.List;
import model.board.Board;
import model.board.LudoBoard;
import model.direction.ClockwiseMovementStrategy;
import model.direction.EntryDirectionAssigner;
import model.direction.MovementDirectionStrategy;
import model.piece.Piece;
import model.player.HomeEntryPolicy;
import model.player.Player;
import model.player.command.MoveCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.Fixtures;

// Every strategy question defaults to "no"; each command only answers "yes" to what it really is.
@DisplayName("Move command answers to strategy questions")
class MoveCommandDefaultsTest {

    private static final MovementDirectionStrategy CLOCKWISE = ClockwiseMovementStrategy.getInstance();
    private static final HomeEntryPolicy ENTRY_ALLOWED = piece -> false;

    private final Board board = LudoBoard.getInstance();
    private final Player red = Fixtures.playerOf(PlayerColor.RED);
    private final Piece piece = Fixtures.placeOnTrackClockwise(red, 0, 10);

    @Nested
    @DisplayName("MovePieceCommand")
    class MovePiece {

        private final MoveCommand command = new MovePieceCommand(red, piece, 4, board, ENTRY_ALLOWED, CLOCKWISE);

        @Test
        void doesNotEnterTheBoard() {
            assertFalse(command.entersBoard());
        }

        @Test
        void isARealMove() {
            assertFalse(command.movesNothing());
        }

        @Test
        void isNotABlockMove() {
            assertFalse(command.movesExistingBlock());
        }

        @Test
        void doesNotBreakABlock() {
            assertFalse(command.breaksExistingBlock());
        }
    }

    @Nested
    @DisplayName("MoveBlockCommand")
    class MoveBlock {

        private final Piece partner = Fixtures.placeOnTrackClockwise(red, 1, 10);
        private final MoveCommand command =
                new MoveBlockCommand(red, List.of(piece, partner), 3, board, ENTRY_ALLOWED, CLOCKWISE);

        @Test
        void doesNotEnterTheBoard() {
            assertFalse(command.entersBoard());
        }

        @Test
        void isARealMove() {
            assertFalse(command.movesNothing());
        }

        @Test
        void doesNotBreakABlock() {
            assertFalse(command.breaksExistingBlock());
        }
    }

    @Nested
    @DisplayName("EnterBoardCommand")
    class EnterBoard {

        private final MoveCommand command =
                new EnterBoardCommand(red, red.getPieces().get(1), board, mock(EntryDirectionAssigner.class));

        @Test
        void isARealMove() {
            assertFalse(command.movesNothing());
        }

        @Test
        void hasNoPreviewedLandingCell() {
            assertTrue(command.previewLandingPosition().isEmpty());
        }

        @Test
        void doesNotReachHomeOrLeaveThePath() {
            assertFalse(command.reachesHome());
            assertFalse(command.leavesStandardPath());
        }

        @Test
        void isNeitherABlockMoveNorABlockBreak() {
            assertFalse(command.movesExistingBlock());
            assertFalse(command.breaksExistingBlock());
        }
    }

    @Nested
    @DisplayName("leaving the track while standing on Approach")
    class StandingOnApproach {

        // Entry is allowed only once one Approach pass has been recorded.
        private final HomeEntryPolicy needsOnePass = candidate -> candidate.getApproachPassCount() < 1;

        @Test
        void aPieceOnApproachWithItsPassRecordedMayLeaveTheTrack() {
            Piece onApproach = Fixtures.placeOnTrackClockwise(red, 2, 26);
            onApproach.recordApproachPass();

            assertTrue(HomeArrivalChecker.leavesTrack(onApproach, 3, board, needsOnePass, CLOCKWISE));
        }

        @Test
        void standingOnApproachAddsNoExtraCrossingToThePassCount() {
            Piece onApproach = Fixtures.placeOnTrackClockwise(red, 2, 26);

            // Already on Approach with no pass recorded: nothing is crossed, so entry is still refused.
            assertFalse(HomeArrivalChecker.leavesTrack(onApproach, 3, board, needsOnePass, CLOCKWISE));
        }
    }
}
