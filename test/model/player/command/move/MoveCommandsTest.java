package model.player.command.move;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import config.enums.CoinTossResult;
import config.enums.PlayerColor;
import java.util.List;
import java.util.Optional;
import message.move.BlockMoved;
import message.move.PieceDirectionAssigned;
import message.move.PieceEnteredBoard;
import message.move.PieceEnteredHomeStraight;
import message.move.PieceLeftBlock;
import message.move.PieceMoved;
import message.move.PieceReachedHome;
import message.observer.GameMessagePublisher;
import model.board.Board;
import model.board.LudoBoard;
import model.direction.ClockwiseMovementStrategy;
import model.direction.CounterClockwiseMovementStrategy;
import model.direction.EntryDirection;
import model.direction.EntryDirectionAssigner;
import model.direction.MovementDirectionStrategy;
import model.piece.Piece;
import model.player.HomeEntryPolicy;
import model.player.Player;
import model.player.command.MoveCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import support.Fixtures;

@DisplayName("Move commands")
class MoveCommandsTest {

    private static final MovementDirectionStrategy CLOCKWISE = ClockwiseMovementStrategy.getInstance();
    private static final MovementDirectionStrategy COUNTER_CLOCKWISE = CounterClockwiseMovementStrategy.getInstance();
    private static final HomeEntryPolicy ENTRY_ALLOWED = piece -> false;
    private static final HomeEntryPolicy ENTRY_FORBIDDEN = piece -> true;

    private final Board board = LudoBoard.getInstance();
    private final Player red = Fixtures.playerOf(PlayerColor.RED);
    private final GameMessagePublisher publisher = mock(GameMessagePublisher.class);

    @Nested
    @DisplayName("EnterBoardCommand")
    class EnterBoard {

        private final EntryDirectionAssigner assigner = mock(EntryDirectionAssigner.class);
        private final Piece piece = red.getPieces().get(0);
        private final MoveCommand command = new EnterBoardCommand(red, piece, board, assigner);

        EnterBoard() {
            when(assigner.assign()).thenReturn(new EntryDirection(CoinTossResult.HEADS, CLOCKWISE));
        }

        @Test
        void thePieceLandsOnItsColorsEntryCell() {
            command.execute(publisher);

            assertTrue(piece.isOnTrack());
            assertEquals(28, piece.getTrackPosition());
        }

        @Test
        void thePieceTakesTheDirectionFromTheCoinToss() {
            command.execute(publisher);

            assertSame(CLOCKWISE, piece.getMovementDirection());
        }

        @Test
        void aTailsTossGivesCounterClockwise() {
            when(assigner.assign()).thenReturn(new EntryDirection(CoinTossResult.TAILS, COUNTER_CLOCKWISE));

            command.execute(publisher);

            assertSame(COUNTER_CLOCKWISE, piece.getMovementDirection());
        }

        @Test
        void entryAndDirectionAreAnnouncedInThatOrder() {
            command.execute(publisher);

            InOrder order = inOrder(publisher);
            order.verify(publisher).publish(new PieceEnteredBoard(PlayerColor.RED, "R1", 28, 1, 3));
            order.verify(publisher).publish(new PieceDirectionAssigned("R1", "Heads", "Clockwise"));
        }

        @Test
        void isRecognisedAsEnteringTheBoard() {
            assertTrue(command.entersBoard());
        }

        @Test
        void reportsThePieceAsAffected() {
            assertSame(piece, command.getAffectedPiece());
        }
    }

    @Nested
    @DisplayName("MovePieceCommand")
    class MovePiece {

        private MoveCommand moveOf(Piece piece, int steps, HomeEntryPolicy policy) {
            return new MovePieceCommand(red, piece, steps, board, policy, CLOCKWISE);
        }

        @Test
        void aTrackMoveIsAnnouncedWithBothEndpoints() {
            Piece piece = Fixtures.placeOnTrackClockwise(red, 0, 10);

            moveOf(piece, 4, ENTRY_ALLOWED).execute(publisher);

            verify(publisher).publish(new PieceMoved(PlayerColor.RED, "R1", 10, 14, 4, "Clockwise"));
            assertEquals(14, piece.getTrackPosition());
        }

        @Test
        void enteringTheHomeStraightIsAnnouncedWithTheCellName() {
            Piece piece = Fixtures.placeOnTrackClockwise(red, 0, 24);

            moveOf(piece, 5, ENTRY_ALLOWED).execute(publisher);

            verify(publisher).publish(new PieceEnteredHomeStraight("R1", "RedHomePath2"));
        }

        @Test
        void reachingHomeIsAnnounced() {
            Piece piece = Fixtures.placeOnHomeStraight(red, 0, 3, CLOCKWISE);

            moveOf(piece, 2, ENTRY_ALLOWED).execute(publisher);

            verify(publisher).publish(new PieceReachedHome("R1"));
            assertTrue(piece.isHome());
        }

        @Test
        void reportsThePieceAsAffected() {
            Piece piece = Fixtures.placeOnTrackClockwise(red, 0, 10);

            assertSame(piece, moveOf(piece, 4, ENTRY_ALLOWED).getAffectedPiece());
        }

        @Test
        void previewShowsTheLandingCellWithoutMovingThePiece() {
            Piece piece = Fixtures.placeOnTrackClockwise(red, 0, 10);

            Optional<Integer> landing = moveOf(piece, 4, ENTRY_ALLOWED).previewLandingPosition();

            assertEquals(Optional.of(14), landing);
            assertEquals(10, piece.getTrackPosition());
        }

        @Test
        void previewIsEmptyWhenTheMovePassesApproach() {
            Piece piece = Fixtures.placeOnTrackClockwise(red, 0, 24);

            assertTrue(moveOf(piece, 5, ENTRY_ALLOWED).previewLandingPosition().isEmpty());
        }

        @Test
        void aMoveFromTheHomeStraightReachesHomeWhenItUsesUpTheLastCells() {
            Piece piece = Fixtures.placeOnHomeStraight(red, 0, 3, CLOCKWISE);

            assertTrue(moveOf(piece, 2, ENTRY_ALLOWED).reachesHome());
            assertFalse(moveOf(piece, 1, ENTRY_ALLOWED).reachesHome());
        }

        @Test
        void aTrackMoveReachesHomeOnlyWhenItGoesRightThroughTheHomeStraight() {
            Piece piece = Fixtures.placeOnTrackClockwise(red, 0, 26);

            assertTrue(moveOf(piece, 6, ENTRY_ALLOWED).reachesHome());
            assertFalse(moveOf(piece, 5, ENTRY_ALLOWED).reachesHome());
        }

        @Test
        void aMoveThatMayNotEnterTheHomeStraightDoesNotReachHome() {
            Piece piece = Fixtures.placeOnTrackClockwise(red, 0, 26);

            assertFalse(moveOf(piece, 6, ENTRY_FORBIDDEN).reachesHome());
        }

        @Test
        void aMoveThatCrossesApproachLeavesTheStandardPath() {
            Piece piece = Fixtures.placeOnTrackClockwise(red, 0, 24);

            assertTrue(moveOf(piece, 5, ENTRY_ALLOWED).leavesStandardPath());
        }

        @Test
        void aMoveThatStopsShortOfApproachStaysOnTheStandardPath() {
            Piece piece = Fixtures.placeOnTrackClockwise(red, 0, 24);

            assertFalse(moveOf(piece, 2, ENTRY_ALLOWED).leavesStandardPath());
        }

        @Test
        void aMoveThatMayNotEnterStaysOnTheStandardPath() {
            Piece piece = Fixtures.placeOnTrackClockwise(red, 0, 24);

            assertFalse(moveOf(piece, 5, ENTRY_FORBIDDEN).leavesStandardPath());
        }
    }

    @Nested
    @DisplayName("MoveBlockCommand")
    class MoveBlock {

        private final Piece first = Fixtures.placeOnTrackClockwise(red, 0, 10);
        private final Piece second = Fixtures.placeOnTrackClockwise(red, 1, 10);

        private MoveCommand moveOf(int steps) {
            return new MoveBlockCommand(red, List.of(first, second), steps, board, ENTRY_ALLOWED, CLOCKWISE);
        }

        @Test
        void everyPieceOfTheBlockMovesTheSameSteps() {
            moveOf(3).execute(publisher);

            assertEquals(13, first.getTrackPosition());
            assertEquals(13, second.getTrackPosition());
        }

        @Test
        void everyPieceAdoptsTheBlocksDirectionAndSize() {
            second.assignMovementDirection(COUNTER_CLOCKWISE);

            moveOf(3).execute(publisher);

            assertSame(CLOCKWISE, second.getMovementDirection());
            assertEquals(2, second.getAdoptedForBlockSize());
        }

        @Test
        void aSameDirectionBlockMoveIsAnnouncedOnce() {
            moveOf(3).execute(publisher);

            verify(publisher).publish(new BlockMoved("R1+R2", 10, 13, "Same-Direction", "Clockwise"));
        }

        @Test
        void aMixedBlockIsAnnouncedAsOppositeDirection() {
            second.assignMovementDirection(COUNTER_CLOCKWISE);

            moveOf(3).execute(publisher);

            verify(publisher).publish(new BlockMoved("R1+R2", 10, 13, "Opposite-Direction", "Clockwise"));
        }

        @Test
        void aBlockEnteringTheHomeStraightIsAnnouncedAsOne() {
            Piece a = Fixtures.placeOnTrackClockwise(red, 2, 24);
            Piece b = Fixtures.placeOnTrackClockwise(red, 3, 24);

            new MoveBlockCommand(red, List.of(a, b), 5, board, ENTRY_ALLOWED, CLOCKWISE).execute(publisher);

            verify(publisher).publish(new PieceEnteredHomeStraight("R3+R4", "RedHomePath2"));
        }

        @Test
        void aBlockReachingHomeIsAnnouncedAsOne() {
            Piece a = Fixtures.placeOnHomeStraight(red, 2, 3, CLOCKWISE);
            Piece b = Fixtures.placeOnHomeStraight(red, 3, 3, CLOCKWISE);

            new MoveBlockCommand(red, List.of(a, b), 2, board, ENTRY_ALLOWED, CLOCKWISE).execute(publisher);

            verify(publisher).publish(new PieceReachedHome("R3+R4"));
            assertTrue(a.isHome() && b.isHome());
        }

        @Test
        void isRecognisedAsMovingAnExistingBlock() {
            assertTrue(moveOf(3).movesExistingBlock());
        }

        @Test
        void reportsTheFirstPieceAsAffected() {
            assertSame(first, moveOf(3).getAffectedPiece());
        }

        @Test
        void previewShowsTheLandingCell() {
            assertEquals(Optional.of(13), moveOf(3).previewLandingPosition());
        }

        @Test
        void reachesHomeFollowsTheFirstPiece() {
            Piece a = Fixtures.placeOnHomeStraight(red, 2, 3, CLOCKWISE);
            Piece b = Fixtures.placeOnHomeStraight(red, 3, 3, CLOCKWISE);
            MoveCommand command = new MoveBlockCommand(red, List.of(a, b), 2, board, ENTRY_ALLOWED, CLOCKWISE);

            assertTrue(command.reachesHome());
        }

        @Test
        void leavesTheStandardPathWhenItCrossesApproach() {
            Piece a = Fixtures.placeOnTrackClockwise(red, 2, 24);
            Piece b = Fixtures.placeOnTrackClockwise(red, 3, 24);
            MoveCommand command = new MoveBlockCommand(red, List.of(a, b), 5, board, ENTRY_ALLOWED, CLOCKWISE);

            assertTrue(command.leavesStandardPath());
        }
    }

    @Nested
    @DisplayName("BreakBlockCommand")
    class BreakBlock {

        private final Piece stays = Fixtures.placeOnTrackClockwise(red, 0, 10);
        private final Piece leaves = Fixtures.placeOnTrackClockwise(red, 1, 10);
        private final MoveCommand releasedMove = mock(MoveCommand.class);

        BreakBlock() {
            when(releasedMove.getAffectedPiece()).thenReturn(leaves);
        }

        private MoveCommand breakOf(List<MoveCommand> moves) {
            return new BreakBlockCommand(red, List.of(stays, leaves), moves);
        }

        @Test
        void everyMemberGoesBackToItsOriginalDirection() {
            leaves.adoptBlockDirection(COUNTER_CLOCKWISE, 2);

            breakOf(List.of()).execute(publisher);

            assertSame(CLOCKWISE, leaves.getMovementDirection());
        }

        @Test
        void everyMemberIsAnnouncedAsLeavingTheBlock() {
            breakOf(List.of()).execute(publisher);

            verify(publisher).publish(new PieceLeftBlock("R1"));
            verify(publisher).publish(new PieceLeftBlock("R2"));
        }

        @Test
        void theReleasedMovesAreExecuted() {
            breakOf(List.of(releasedMove)).execute(publisher);

            verify(releasedMove).execute(publisher);
        }

        @Test
        void aBreakIsRecognisedAsBreakingABlock() {
            assertTrue(breakOf(List.of()).breaksExistingBlock());
        }

        @Test
        void theAffectedPiecesAreTheOnesThatMoved() {
            assertEquals(List.of(leaves), breakOf(List.of(releasedMove)).getAffectedPieces());
        }

        @Test
        void withNoMovesTheFirstMemberIsTheAffectedPiece() {
            MoveCommand command = breakOf(List.of());

            assertEquals(List.of(stays), command.getAffectedPieces());
            assertSame(stays, command.getAffectedPiece());
        }

        @Test
        void leavesTheStandardPathWhenAReleasedMoveDoes() {
            when(releasedMove.leavesStandardPath()).thenReturn(true);

            assertTrue(breakOf(List.of(releasedMove)).leavesStandardPath());
        }

        @Test
        void staysOnTheStandardPathWhenNoReleasedMoveLeaves() {
            assertFalse(breakOf(List.of(releasedMove)).leavesStandardPath());
            assertFalse(breakOf(List.of()).leavesStandardPath());
        }
    }
}
