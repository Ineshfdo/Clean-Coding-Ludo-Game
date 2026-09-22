package model.effect.destination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import config.enums.PlayerColor;
import java.util.List;
import message.mystery.BetaRestrictionApplied;
import message.mystery.PieceTeleported;
import message.observer.GameMessagePublisher;
import model.board.LudoBoard;
import model.effect.activation.EffectActivationRule;
import model.effect.activation.MysteryCellArrival;
import model.effect.activation.MysteryTeleportActivationRule;
import model.effect.mysterycell.MysteryCellDestination;
import model.effect.rule.AlphaEffectRule;
import model.effect.rule.GammaDirectionRule;
import model.piece.Piece;
import model.player.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.Fixtures;

@DisplayName("Mystery Cell destinations")
class DestinationsTest {

    private final LudoBoard board = LudoBoard.getInstance();
    private final Player red = Fixtures.playerOf(PlayerColor.RED);
    private final GameMessagePublisher publisher = mock(GameMessagePublisher.class);
    private final EffectActivationRule allowsEffects = new MysteryTeleportActivationRule();

    private static final EffectActivationRule REFUSES_EFFECTS = new EffectActivationRule() {
        @Override
        protected boolean isSatisfiedBy(MysteryCellArrival arrival) {
            return false;
        }
    };

    private Piece pieceOnTrack(int index) {
        return Fixtures.placeOnTrackClockwise(red, index, 5);
    }

    @Nested
    @DisplayName("AlphaDestination")
    class Alpha {

        private final AlphaEffectRule alphaEffectRule = mock(AlphaEffectRule.class);
        private final MysteryCellDestination alpha = new AlphaDestination(board, allowsEffects, alphaEffectRule);

        @Test
        void isLabelledAlpha() {
            assertEquals("Alpha", alpha.getLabel());
        }

        @Test
        void placesEveryPieceOnTheAlphaCell() {
            Piece first = pieceOnTrack(0);
            Piece second = pieceOnTrack(1);

            alpha.receive(red, List.of(first, second), publisher);

            assertEquals(9, first.getTrackPosition());
            assertEquals(9, second.getTrackPosition());
        }

        @Test
        void announcesTheTeleport() {
            Piece piece = pieceOnTrack(0);

            alpha.receive(red, List.of(piece), publisher);

            verify(publisher).publish(new PieceTeleported("R1", "Alpha", 9));
        }

        @Test
        void handsTheArrivedPiecesToTheAlphaEffectRule() {
            List<Piece> pieces = List.of(pieceOnTrack(0));

            alpha.receive(red, pieces, publisher);

            verify(alphaEffectRule).applyTo(red, pieces, publisher);
        }

        @Test
        void appliesNoEffectWhenActivationIsRefused() {
            MysteryCellDestination refused = new AlphaDestination(board, REFUSES_EFFECTS, alphaEffectRule);
            List<Piece> pieces = List.of(pieceOnTrack(0));

            refused.receive(red, pieces, publisher);

            verify(alphaEffectRule, never()).applyTo(red, pieces, publisher);
        }
    }

    @Nested
    @DisplayName("BetaDestination")
    class Beta {

        private final MysteryCellDestination beta = new BetaDestination(board, allowsEffects);

        @Test
        void isLabelledBeta() {
            assertEquals("Beta", beta.getLabel());
        }

        @Test
        void placesThePiecesOnTheBetaCell() {
            Piece piece = pieceOnTrack(0);

            beta.receive(red, List.of(piece), publisher);

            assertEquals(27, piece.getTrackPosition());
            verify(publisher).publish(new PieceTeleported("R1", "Beta", 27));
        }

        @Test
        void restrictsEveryTeleportedPiece() {
            Piece first = pieceOnTrack(0);
            Piece second = pieceOnTrack(1);

            beta.receive(red, List.of(first, second), publisher);

            assertTrue(first.getRestrictionState().forbidsMovement());
            assertTrue(second.getRestrictionState().forbidsMovement());
        }

        @Test
        void announcesTheRestrictionForTheWholeGroup() {
            beta.receive(red, List.of(pieceOnTrack(0), pieceOnTrack(1)), publisher);

            verify(publisher).publish(new BetaRestrictionApplied("R1+R2"));
        }

        @Test
        void doesNotRestrictWhenActivationIsRefused() {
            Piece piece = pieceOnTrack(0);

            new BetaDestination(board, REFUSES_EFFECTS).receive(red, List.of(piece), publisher);

            assertFalse(piece.getRestrictionState().forbidsMovement());
        }
    }

    @Nested
    @DisplayName("GammaDestination")
    class Gamma {

        private final GammaDirectionRule gammaDirectionRule = mock(GammaDirectionRule.class);
        private final MysteryCellDestination gamma = new GammaDestination(board, allowsEffects, gammaDirectionRule);

        @Test
        void isLabelledGamma() {
            assertEquals("Gamma", gamma.getLabel());
        }

        @Test
        void placesThePiecesOnTheGammaCell() {
            Piece piece = pieceOnTrack(0);

            gamma.receive(red, List.of(piece), publisher);

            assertEquals(46, piece.getTrackPosition());
            verify(publisher).publish(new PieceTeleported("R1", "Gamma", 46));
        }

        @Test
        void handsTheArrivedPiecesToTheGammaDirectionRule() {
            List<Piece> pieces = List.of(pieceOnTrack(0));

            gamma.receive(red, pieces, publisher);

            verify(gammaDirectionRule).applyTo(red, pieces, publisher);
        }
    }

    @Nested
    @DisplayName("EntryDestination")
    class Entry {

        private final MysteryCellDestination entry = new EntryDestination(board, allowsEffects);

        @Test
        void isLabelledEntry() {
            assertEquals("Entry", entry.getLabel());
        }

        @Test
        void placesThePiecesOnTheirOwnEntryCell() {
            Piece piece = pieceOnTrack(0);

            entry.receive(red, List.of(piece), publisher);

            assertEquals(28, piece.getTrackPosition());
            verify(publisher).publish(new PieceTeleported("R1", "Entry", 28));
        }

        @Test
        void usesTheEntryCellOfTheTeleportedPlayersColor() {
            Player yellow = Fixtures.playerOf(PlayerColor.YELLOW);
            Piece piece = Fixtures.placeOnTrackClockwise(yellow, 0, 20);

            entry.receive(yellow, List.of(piece), publisher);

            assertEquals(2, piece.getTrackPosition());
        }
    }

    @Nested
    @DisplayName("ApproachDestination")
    class Approach {

        private final MysteryCellDestination approach = new ApproachDestination(board, allowsEffects);

        @Test
        void isLabelledApproach() {
            assertEquals("Approach", approach.getLabel());
        }

        @Test
        void placesThePiecesOnTheirOwnApproachCell() {
            Piece piece = pieceOnTrack(0);

            approach.receive(red, List.of(piece), publisher);

            assertEquals(26, piece.getTrackPosition());
            verify(publisher).publish(new PieceTeleported("R1", "Approach", 26));
        }

        @Test
        void countsTheArrivalAsAnApproachPass() {
            Piece piece = pieceOnTrack(0);

            approach.receive(red, List.of(piece), publisher);

            assertEquals(1, piece.getApproachPassCount());
        }
    }

    @Nested
    @DisplayName("BaseDestination")
    class Base {

        private final MysteryCellDestination base = new BaseDestination();

        @Test
        void isLabelledBase() {
            assertEquals("Base", base.getLabel());
        }

        @Test
        void sendsEveryPieceBackToBase() {
            Piece first = pieceOnTrack(0);
            Piece second = pieceOnTrack(1);

            base.receive(red, List.of(first, second), publisher);

            assertTrue(first.isAtBase());
            assertTrue(second.isAtBase());
        }

        @Test
        void announcesTheTeleportWithNoTrackCell() {
            base.receive(red, List.of(pieceOnTrack(0), pieceOnTrack(1)), publisher);

            verify(publisher).publish(new PieceTeleported("R1+R2", "Base", -1));
        }
    }
}
