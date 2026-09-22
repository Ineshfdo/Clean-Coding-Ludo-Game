package model.effect.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import config.enums.CoinTossResult;
import config.enums.PlayerColor;
import java.util.List;
import message.mystery.BlockEffectAssigned;
import message.mystery.IndividualEffectAssigned;
import message.mystery.PieceDirectionReversed;
import message.observer.GameMessagePublisher;
import model.direction.ClockwiseMovementStrategy;
import model.direction.CounterClockwiseMovementStrategy;
import model.effect.mysterycell.MysteryCellDestination;
import model.piece.Piece;
import model.player.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.Fixtures;
import utils.coin.CoinToss;

@DisplayName("Mystery Cell effect rules")
class EffectRulesTest {

    private final Player player = Fixtures.playerOf(PlayerColor.RED);
    private final GameMessagePublisher publisher = mock(GameMessagePublisher.class);

    @Nested
    @DisplayName("AlphaEffectRule")
    class Alpha {

        private final CoinToss coinToss = mock(CoinToss.class);
        private final AlphaEffectRule rule = new AlphaEffectRule(coinToss);

        @Test
        void headsGivesASinglePieceTheEnergizedEffect() {
            Piece piece = player.getPieces().get(0);
            when(coinToss.flip()).thenReturn(CoinTossResult.HEADS);

            rule.applyTo(player, List.of(piece), publisher);

            assertEquals("Energized", piece.getIndividualEffect().getLabel());
        }

        @Test
        void tailsGivesASinglePieceTheSickEffect() {
            Piece piece = player.getPieces().get(0);
            when(coinToss.flip()).thenReturn(CoinTossResult.TAILS);

            rule.applyTo(player, List.of(piece), publisher);

            assertEquals("Sick", piece.getIndividualEffect().getLabel());
        }

        @Test
        void aSinglePieceIsAnnouncedOnceAndGetsNoBlockEffect() {
            Piece piece = player.getPieces().get(0);
            when(coinToss.flip()).thenReturn(CoinTossResult.HEADS);

            rule.applyTo(player, List.of(piece), publisher);

            verify(publisher).publish(new IndividualEffectAssigned("R1", "Energized"));
            verify(publisher, never()).publish(any(BlockEffectAssigned.class));
            assertFalse(piece.getBlockEffect().isActive());
        }

        @Test
        void everyPieceOfABlockGetsItsOwnEffectFromItsOwnToss() {
            Piece first = player.getPieces().get(0);
            Piece second = player.getPieces().get(1);
            // Two individual tosses, then one for the block.
            when(coinToss.flip()).thenReturn(CoinTossResult.HEADS, CoinTossResult.TAILS, CoinTossResult.HEADS);

            rule.applyTo(player, List.of(first, second), publisher);

            assertEquals("Energized", first.getIndividualEffect().getLabel());
            assertEquals("Sick", second.getIndividualEffect().getLabel());
        }

        @Test
        void aBlockAlsoGetsASharedEffectFromTheThirdToss() {
            Piece first = player.getPieces().get(0);
            Piece second = player.getPieces().get(1);
            when(coinToss.flip()).thenReturn(CoinTossResult.HEADS, CoinTossResult.HEADS, CoinTossResult.TAILS);

            rule.applyTo(player, List.of(first, second), publisher);

            assertTrue(first.hasActiveBlockEffectForSize(2));
            assertTrue(second.hasActiveBlockEffectForSize(2));
            assertEquals("Sick", first.getBlockEffect().getLabel());
            verify(publisher).publish(new BlockEffectAssigned("R1+R2", "Sick"));
        }

        @Test
        void effectsLastFourRounds() {
            Piece piece = player.getPieces().get(0);
            when(coinToss.flip()).thenReturn(CoinTossResult.HEADS);

            rule.applyTo(player, List.of(piece), publisher);

            for (int round = 1; round <= 4; round++) {
                piece.tickIndividualEffect();
            }
            assertTrue(piece.getIndividualEffect().isActive());

            piece.tickIndividualEffect();
            assertFalse(piece.getIndividualEffect().isActive());
        }
    }

    @Nested
    @DisplayName("GammaDirectionRule")
    class Gamma {

        private final MysteryCellDestination betaDestination = mock(MysteryCellDestination.class);
        private final GammaDirectionRule rule = new GammaDirectionRule(betaDestination);

        @Test
        void aClockwiseGroupIsReversedToCounterClockwise() {
            Piece piece = Fixtures.placeOnTrack(player, 0, 46, ClockwiseMovementStrategy.getInstance());

            rule.applyTo(player, List.of(piece), publisher);

            assertSame(CounterClockwiseMovementStrategy.getInstance(), piece.getMovementDirection());
        }

        @Test
        void reversalIsPermanentSoTheOriginalDirectionChangesToo() {
            Piece piece = Fixtures.placeOnTrack(player, 0, 46, ClockwiseMovementStrategy.getInstance());

            rule.applyTo(player, List.of(piece), publisher);

            assertSame(CounterClockwiseMovementStrategy.getInstance(), piece.getOriginalMovementDirection());
        }

        @Test
        void aReversedClockwiseGroupIsAnnounced() {
            Piece first = Fixtures.placeOnTrack(player, 0, 46, ClockwiseMovementStrategy.getInstance());
            Piece second = Fixtures.placeOnTrack(player, 1, 46, ClockwiseMovementStrategy.getInstance());

            rule.applyTo(player, List.of(first, second), publisher);

            verify(publisher).publish(new PieceDirectionReversed("R1+R2", "Counter-Clockwise"));
        }

        @Test
        void aClockwiseGroupIsNotSentOnToBeta() {
            Piece piece = Fixtures.placeOnTrack(player, 0, 46, ClockwiseMovementStrategy.getInstance());

            rule.applyTo(player, List.of(piece), publisher);

            verifyNoInteractions(betaDestination);
        }

        @Test
        void aCounterClockwiseGroupIsSentOnToBeta() {
            Piece piece = Fixtures.placeOnTrack(player, 0, 46, CounterClockwiseMovementStrategy.getInstance());
            List<Piece> group = List.of(piece);

            rule.applyTo(player, group, publisher);

            verify(betaDestination).receive(player, group, publisher);
        }

        @Test
        void aCounterClockwiseGroupKeepsItsDirection() {
            Piece piece = Fixtures.placeOnTrack(player, 0, 46, CounterClockwiseMovementStrategy.getInstance());

            rule.applyTo(player, List.of(piece), publisher);

            assertSame(CounterClockwiseMovementStrategy.getInstance(), piece.getMovementDirection());
        }
    }
}
