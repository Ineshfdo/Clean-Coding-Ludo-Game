package model.player.strategy.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import config.enums.PlayerColor;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import model.piece.Piece;
import model.player.Player;
import model.player.command.MoveCommand;
import model.player.strategy.StrategyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.Commands;
import support.Fixtures;

@DisplayName("Strategy helpers")
class StrategyHelpersTest {

    private final Player blue = Fixtures.playerOf(PlayerColor.BLUE);

    @Nested
    @DisplayName("BluePieceRotationIterator")
    class Rotation {

        private final List<Piece> pieces = blue.getPieces();

        @Test
        void startsAtTheFirstPieceWhenNothingMovedYet() {
            assertSame(pieces.get(0), new BluePieceRotationIterator(pieces).next());
        }

        @Test
        void visitsThePiecesInOrder() {
            BluePieceRotationIterator rotation = new BluePieceRotationIterator(pieces);

            assertSame(pieces.get(0), rotation.next());
            assertSame(pieces.get(1), rotation.next());
            assertSame(pieces.get(2), rotation.next());
            assertSame(pieces.get(3), rotation.next());
        }

        @Test
        void wrapsAroundAfterTheLastPiece() {
            BluePieceRotationIterator rotation = new BluePieceRotationIterator(pieces);
            for (int index = 0; index < 4; index++) {
                rotation.next();
            }

            assertSame(pieces.get(0), rotation.next());
        }

        @Test
        void resumesAfterTheLastMovedPiece() {
            BluePieceRotationIterator rotation = new BluePieceRotationIterator(pieces, pieces.get(1));

            assertSame(pieces.get(2), rotation.next());
        }

        @Test
        void resumesAtTheFirstPieceAfterTheLastOneMoved() {
            BluePieceRotationIterator rotation = new BluePieceRotationIterator(pieces, pieces.get(3));

            assertSame(pieces.get(0), rotation.next());
        }

        @Test
        void neverRunsOutWhilePiecesExist() {
            assertTrue(new BluePieceRotationIterator(pieces).hasNext());
        }

        @Test
        void hasNothingToOfferForNoPieces() {
            assertFalse(new BluePieceRotationIterator(List.of()).hasNext());
        }

        @Test
        void asksForAPieceWhenThereAreNoneIsAnError() {
            BluePieceRotationIterator rotation = new BluePieceRotationIterator(List.of());

            assertThrows(NoSuchElementException.class, rotation::next);
        }
    }

    @Nested
    @DisplayName("CommandFinder")
    class Finder {

        private final MoveCommand first = Commands.forPiece(blue.getPieces().get(0));
        private final MoveCommand second = Commands.forPiece(blue.getPieces().get(1));

        @Test
        void findsTheFirstCommandThatMatches() {
            Optional<MoveCommand> found = CommandFinder.findFirst(List.of(first, second), command -> true);

            assertSame(first, found.orElseThrow());
        }

        @Test
        void skipsCommandsThatDoNotMatch() {
            Optional<MoveCommand> found =
                    CommandFinder.findFirst(List.of(first, second), command -> command == second);

            assertSame(second, found.orElseThrow());
        }

        @Test
        void isEmptyWhenNothingMatches() {
            assertTrue(CommandFinder.findFirst(List.of(first, second), command -> false).isEmpty());
        }
    }

    @Nested
    @DisplayName("CaptureTargetFinder")
    class CaptureTarget {

        private final Player red = Fixtures.playerOf(PlayerColor.RED);
        private final Player green = Fixtures.playerOf(PlayerColor.GREEN);
        private final List<Player> everyone = List.of(red, green);
        private final StrategyContext context = Commands.contextFor(red, everyone, 1);

        @Test
        void aMoveWithoutALandingCellCapturesNothing() {
            Piece mover = Fixtures.placeOnTrackClockwise(red, 0, 10);

            assertTrue(CaptureTargetFinder.findTarget(Commands.movingTo(mover, null), context).isEmpty());
        }

        @Test
        void aLonePieceLandingOnAnOpponentTargetsIt() {
            Piece mover = Fixtures.placeOnTrackClockwise(red, 0, 10);
            Piece victim = Fixtures.placeOnTrackClockwise(green, 0, 14);

            Optional<Piece> target = CaptureTargetFinder.findTarget(Commands.movingTo(mover, 14), context);

            assertSame(victim, target.orElseThrow());
        }

        @Test
        void landingOnAnEmptyCellTargetsNothing() {
            Piece mover = Fixtures.placeOnTrackClockwise(red, 0, 10);
            Fixtures.placeOnTrackClockwise(green, 0, 15);

            assertTrue(CaptureTargetFinder.findTarget(Commands.movingTo(mover, 14), context).isEmpty());
        }

        @Test
        void aPlayersOwnPiecesAreNeverTargeted() {
            Piece mover = Fixtures.placeOnTrackClockwise(red, 0, 10);
            Fixtures.placeOnTrackClockwise(red, 1, 14);

            assertTrue(CaptureTargetFinder.findTarget(Commands.movingTo(mover, 14), context).isEmpty());
        }

        @Test
        void aBlockTargetsAnOpponentBlockOfTheSameSize() {
            Piece mover = Fixtures.placeOnTrackClockwise(red, 0, 10);
            Fixtures.placeOnTrackClockwise(red, 1, 10);
            Piece victim = Fixtures.placeOnTrackClockwise(green, 0, 14);
            Fixtures.placeOnTrackClockwise(green, 1, 14);

            assertSame(victim, CaptureTargetFinder.findTarget(Commands.movingTo(mover, 14), context).orElseThrow());
        }

        @Test
        void aBlockDoesNotTargetAnOpponentOfADifferentSize() {
            Piece mover = Fixtures.placeOnTrackClockwise(red, 0, 10);
            Fixtures.placeOnTrackClockwise(red, 1, 10);
            Fixtures.placeOnTrackClockwise(green, 0, 14);

            assertTrue(CaptureTargetFinder.findTarget(Commands.movingTo(mover, 14), context).isEmpty());
        }

        @Test
        void aPreviewDoesNotMoveAnyPiece() {
            Piece mover = Fixtures.placeOnTrackClockwise(red, 0, 10);
            Fixtures.placeOnTrackClockwise(green, 0, 14);

            CaptureTargetFinder.findTarget(Commands.movingTo(mover, 14), context);

            assertEquals(10, mover.getTrackPosition());
        }
    }
}
