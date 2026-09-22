package model.player.rule.capture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import config.enums.PlayerColor;
import java.util.List;
import java.util.Optional;
import model.direction.ClockwiseMovementStrategy;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.command.capture.CaptureBlockCommand;
import model.player.command.capture.CapturePieceCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.Fixtures;

@DisplayName("Capture rules")
class CaptureRulesTest {

    private final Player red = Fixtures.playerOf(PlayerColor.RED);
    private final Player green = Fixtures.playerOf(PlayerColor.GREEN);
    private final List<Player> everyone = List.of(red, green);

    @Nested
    @DisplayName("PieceCaptureRule")
    class PieceCapture {

        private final CaptureCheckRule rule = new PieceCaptureRule();

        @Test
        void aLonePieceLandingOnALoneOpponentCapturesIt() {
            Piece mover = Fixtures.placeOnTrackClockwise(red, 0, 20);
            Piece victim = Fixtures.placeOnTrackClockwise(green, 0, 20);

            Optional<Command> capture = rule.findCapture(red, mover, everyone);

            assertInstanceOf(CapturePieceCommand.class, capture.orElseThrow());
            assertSame(victim, capture.orElseThrow().getAffectedPiece());
        }

        @Test
        void nothingIsCapturedWhenTheOpponentIsOnAnotherCell() {
            Piece mover = Fixtures.placeOnTrackClockwise(red, 0, 20);
            Fixtures.placeOnTrackClockwise(green, 0, 21);

            assertTrue(rule.findCapture(red, mover, everyone).isEmpty());
        }

        @Test
        void nothingIsCapturedWhenNoOpponentIsThere() {
            Piece mover = Fixtures.placeOnTrackClockwise(red, 0, 20);

            assertTrue(rule.findCapture(red, mover, everyone).isEmpty());
        }

        @Test
        void aPieceOnTheHomeStraightCapturesNothing() {
            Piece mover = Fixtures.placeOnHomeStraight(red, 0, 2, ClockwiseMovementStrategy.getInstance());

            assertTrue(rule.findCapture(red, mover, everyone).isEmpty());
        }

        @Test
        void anOpponentBlockadeCannotBeCapturedByALonePiece() {
            Piece mover = Fixtures.placeOnTrackClockwise(red, 0, 20);
            Fixtures.placeOnTrackClockwise(green, 0, 20);
            Fixtures.placeOnTrackClockwise(green, 1, 20);

            assertTrue(rule.findCapture(red, mover, everyone).isEmpty());
        }

        @Test
        void aBlockadeMoverLeavesTheCaptureToTheBlockRule() {
            Piece mover = Fixtures.placeOnTrackClockwise(red, 0, 20);
            Fixtures.placeOnTrackClockwise(red, 1, 20);
            Fixtures.placeOnTrackClockwise(green, 0, 20);

            assertTrue(rule.findCapture(red, mover, everyone).isEmpty());
        }

        @Test
        void aPlayersOwnPiecesAreNeverCaptured() {
            Piece mover = Fixtures.placeOnTrackClockwise(red, 0, 20);

            assertTrue(rule.findCapture(red, mover, List.of(red)).isEmpty());
        }
    }

    @Nested
    @DisplayName("BlockCaptureRule")
    class BlockCapture {

        private final CaptureCheckRule rule = new BlockCaptureRule();

        @Test
        void aBlockadeCapturesAnEqualSizedOpponentBlockade() {
            Piece mover = Fixtures.placeOnTrackClockwise(red, 0, 20);
            Fixtures.placeOnTrackClockwise(red, 1, 20);
            Piece victim = Fixtures.placeOnTrackClockwise(green, 0, 20);
            Fixtures.placeOnTrackClockwise(green, 1, 20);

            Optional<Command> capture = rule.findCapture(red, mover, everyone);

            assertInstanceOf(CaptureBlockCommand.class, capture.orElseThrow());
            assertEquals(green.getPiecesAt(20), capture.orElseThrow().getAffectedPieces());
            assertSame(victim, capture.orElseThrow().getAffectedPiece());
        }

        @Test
        void aBlockadeDoesNotCaptureABlockadeOfADifferentSize() {
            Piece mover = Fixtures.placeOnTrackClockwise(red, 0, 20);
            Fixtures.placeOnTrackClockwise(red, 1, 20);
            Fixtures.placeOnTrackClockwise(green, 0, 20);
            Fixtures.placeOnTrackClockwise(green, 1, 20);
            Fixtures.placeOnTrackClockwise(green, 2, 20);

            assertTrue(rule.findCapture(red, mover, everyone).isEmpty());
        }

        @Test
        void aLonePieceIsNotABlockade() {
            Piece mover = Fixtures.placeOnTrackClockwise(red, 0, 20);
            Fixtures.placeOnTrackClockwise(green, 0, 20);

            assertTrue(rule.findCapture(red, mover, everyone).isEmpty());
        }

        @Test
        void aPieceOffTheTrackCapturesNothing() {
            Piece mover = red.getPieces().get(0);

            assertTrue(rule.findCapture(red, mover, everyone).isEmpty());
        }

        @Test
        void aBlockadeAloneOnACellCapturesNothing() {
            Piece mover = Fixtures.placeOnTrackClockwise(red, 0, 20);
            Fixtures.placeOnTrackClockwise(red, 1, 20);

            assertTrue(rule.findCapture(red, mover, everyone).isEmpty());
        }
    }

    @Nested
    @DisplayName("the capture chain (block rule, then piece rule)")
    class Chain {

        private final CaptureCheckRule chain = buildChain();

        private CaptureCheckRule buildChain() {
            CaptureCheckRule blockRule = new BlockCaptureRule();
            blockRule.setNext(new PieceCaptureRule());
            return blockRule;
        }

        @Test
        void aLonePieceFallsThroughToThePieceRule() {
            Piece mover = Fixtures.placeOnTrackClockwise(red, 0, 20);
            Fixtures.placeOnTrackClockwise(green, 0, 20);

            assertInstanceOf(CapturePieceCommand.class, chain.findCapture(red, mover, everyone).orElseThrow());
        }

        @Test
        void aBlockadeIsHandledByTheBlockRule() {
            Piece mover = Fixtures.placeOnTrackClockwise(red, 0, 20);
            Fixtures.placeOnTrackClockwise(red, 1, 20);
            Fixtures.placeOnTrackClockwise(green, 0, 20);
            Fixtures.placeOnTrackClockwise(green, 1, 20);

            assertInstanceOf(CaptureBlockCommand.class, chain.findCapture(red, mover, everyone).orElseThrow());
        }

        @Test
        void noRuleApplyingGivesNoCapture() {
            Piece mover = Fixtures.placeOnTrackClockwise(red, 0, 20);

            assertTrue(chain.findCapture(red, mover, everyone).isEmpty());
        }
    }
}
