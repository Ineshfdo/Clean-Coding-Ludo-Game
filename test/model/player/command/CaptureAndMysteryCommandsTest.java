package model.player.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import config.enums.PlayerColor;
import java.util.List;
import message.capture.BlockCaptured;
import message.capture.PieceCaptured;
import message.mystery.BetaRestrictionTriggered;
import message.observer.GameMessagePublisher;
import model.effect.mysterycell.MysteryCellDestination;
import model.piece.Piece;
import model.player.Player;
import model.player.command.capture.CaptureBlockCommand;
import model.player.command.capture.CapturePieceCommand;
import model.player.command.mystery.BetaReturnToBaseCommand;
import model.player.command.mystery.MysteryCellTeleportCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.Fixtures;

@DisplayName("Capture and Mystery commands")
class CaptureAndMysteryCommandsTest {

    private final Player red = Fixtures.playerOf(PlayerColor.RED);
    private final Player green = Fixtures.playerOf(PlayerColor.GREEN);
    private final GameMessagePublisher publisher = mock(GameMessagePublisher.class);

    @Nested
    @DisplayName("CapturePieceCommand")
    class CapturePiece {

        private final Piece capturer = Fixtures.placeOnTrackClockwise(red, 0, 21);
        private final Piece victim = Fixtures.placeOnTrackClockwise(green, 1, 21);

        private final Command command = new CapturePieceCommand(red, capturer, green, victim);

        @Test
        void theCapturedPieceGoesBackToBase() {
            command.execute(publisher);

            assertTrue(victim.isAtBase());
        }

        @Test
        void theCapturerGainsOneCapture() {
            command.execute(publisher);

            assertEquals(1, capturer.getCaptureCount());
        }

        @Test
        void theCapturerStaysWhereItIs() {
            command.execute(publisher);

            assertEquals(21, capturer.getTrackPosition());
        }

        @Test
        void theCaptureIsAnnouncedWithTheVictimsRemainingPieceCounts() {
            Fixtures.placeOnTrackClockwise(green, 0, 30);

            command.execute(publisher);

            verify(publisher).publish(new PieceCaptured("R1", 21, "G2", PlayerColor.GREEN, 1, 3));
        }

        @Test
        void theAffectedPieceIsTheCapturedOne() {
            assertSame(victim, command.getAffectedPiece());
            assertEquals(List.of(victim), command.getAffectedPieces());
        }
    }

    @Nested
    @DisplayName("CaptureBlockCommand")
    class CaptureBlock {

        private final List<Piece> capturers = List.of(
                Fixtures.placeOnTrackClockwise(red, 0, 20), Fixtures.placeOnTrackClockwise(red, 1, 20));
        private final List<Piece> victims = List.of(
                Fixtures.placeOnTrackClockwise(green, 0, 20), Fixtures.placeOnTrackClockwise(green, 1, 20));

        private final Command command = new CaptureBlockCommand(red, capturers, green, victims);

        @Test
        void everyCapturedPieceGoesBackToBase() {
            command.execute(publisher);

            assertTrue(victims.get(0).isAtBase());
            assertTrue(victims.get(1).isAtBase());
        }

        @Test
        void everyCapturingPieceGainsOneCapture() {
            command.execute(publisher);

            assertEquals(1, capturers.get(0).getCaptureCount());
            assertEquals(1, capturers.get(1).getCaptureCount());
        }

        @Test
        void theCaptureIsAnnouncedForBothBlocks() {
            command.execute(publisher);

            verify(publisher).publish(new BlockCaptured("R1+R2", "G1+G2"));
        }

        @Test
        void theAffectedPiecesAreTheCapturedBlock() {
            assertEquals(victims, command.getAffectedPieces());
            assertSame(victims.get(0), command.getAffectedPiece());
        }
    }

    @Nested
    @DisplayName("BetaReturnToBaseCommand")
    class BetaReturnToBase {

        private final List<Piece> pieces = List.of(
                Fixtures.placeOnTrackClockwise(red, 0, 27), Fixtures.placeOnTrackClockwise(red, 1, 27));
        private final Command command = new BetaReturnToBaseCommand(red, pieces);

        @Test
        void everyPieceReturnsToBase() {
            command.execute(publisher);

            assertTrue(pieces.get(0).isAtBase());
            assertTrue(pieces.get(1).isAtBase());
        }

        @Test
        void theReturnIsAnnounced() {
            command.execute(publisher);

            verify(publisher).publish(new BetaRestrictionTriggered("R1+R2"));
        }

        @Test
        void theAffectedPiecesAreTheReturningOnes() {
            assertEquals(pieces, command.getAffectedPieces());
            assertSame(pieces.get(0), command.getAffectedPiece());
        }
    }

    @Nested
    @DisplayName("MysteryCellTeleportCommand")
    class MysteryCellTeleport {

        private final MysteryCellDestination destination = mock(MysteryCellDestination.class);
        private final List<Piece> pieces = List.of(Fixtures.placeOnTrackClockwise(red, 0, 30));
        private final Command command = new MysteryCellTeleportCommand(red, pieces, destination);

        @Test
        void executingHandsThePiecesToTheDestination() {
            command.execute(publisher);

            verify(destination).receive(red, pieces, publisher);
        }

        @Test
        void theAffectedPiecesAreTheTeleportedOnes() {
            assertEquals(pieces, command.getAffectedPieces());
            assertSame(pieces.get(0), command.getAffectedPiece());
        }
    }
}
