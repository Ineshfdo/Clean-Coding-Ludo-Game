package model.player.rule.mystery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import config.enums.PlayerColor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import message.mystery.BetaRestrictionTriggered;
import message.observer.GameMessagePublisher;
import model.effect.mysterycell.MysteryCellDestination;
import model.effect.mysterycell.MysteryCellLocation;
import model.effect.restriction.BetaRestrictedState;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.command.mystery.MysteryCellTeleportCommand;
import model.player.rule.roll.RollEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.Fixtures;
import utils.randomgenerator.RandomNumberGenerator;

@DisplayName("Mystery rules")
class MysteryRulesTest {

    private final Player red = Fixtures.playerOf(PlayerColor.RED);
    private final GameMessagePublisher publisher = mock(GameMessagePublisher.class);

    @Nested
    @DisplayName("BetaRestrictionRule")
    class BetaRestriction {

        private final BetaRestrictionRule rule = new BetaRestrictionRule();
        private final Piece restricted = Fixtures.placeOnTrackClockwise(red, 0, 27);

        BetaRestriction() {
            red.applyRestriction(restricted, new BetaRestrictedState());
        }

        private void roll(int rollNumber, int rollValue) {
            rule.onRollAccepted(new RollEvent(red, List.of(red), rollNumber, rollValue), publisher);
        }

        @Test
        void oneFirstRollOfThreeDoesNotSendThePieceBack() {
            roll(1, 3);

            assertTrue(restricted.isOnTrack());
            verifyNoInteractions(publisher);
        }

        @Test
        void twoFirstRollsOfThreeInARowSendThePieceBackToBase() {
            roll(1, 3);
            roll(1, 3);

            assertTrue(restricted.isAtBase());
        }

        @Test
        void thePieceReturnIsAnnounced() {
            roll(1, 3);
            roll(1, 3);

            verify(publisher).publish(new BetaRestrictionTriggered("R1"));
        }

        @Test
        void aDifferentRollInBetweenBreaksTheRun() {
            roll(1, 3);
            roll(1, 5);
            roll(1, 3);

            assertTrue(restricted.isOnTrack());
        }

        @Test
        void bonusRollsAreNotCounted() {
            roll(2, 3);
            roll(2, 3);

            assertTrue(restricted.isOnTrack());
        }

        @Test
        void aPlayerWithoutARestrictedPieceIsUnaffected() {
            Player yellow = Fixtures.playerOf(PlayerColor.YELLOW);
            Piece free = Fixtures.placeOnTrackClockwise(yellow, 0, 10);

            rule.onRollAccepted(new RollEvent(yellow, List.of(yellow), 1, 3), publisher);
            rule.onRollAccepted(new RollEvent(yellow, List.of(yellow), 1, 3), publisher);

            assertTrue(free.isOnTrack());
            verifyNoInteractions(publisher);
        }
    }

    @Nested
    @DisplayName("MysteryCellTeleportRule")
    class MysteryCellTeleport {

        private static final int MYSTERY_CELL = 30;

        private final MysteryCellLocation location = mock(MysteryCellLocation.class);
        private final MysteryCellDestination first = mock(MysteryCellDestination.class);
        private final MysteryCellDestination second = mock(MysteryCellDestination.class);
        // Always draws index 1, so the second destination is the one chosen.
        private final List<int[]> drawnRanges = new ArrayList<>();
        private final RandomNumberGenerator generator = (min, max) -> {
            drawnRanges.add(new int[] {min, max});
            return 1;
        };
        private final TeleportRule rule = new MysteryCellTeleportRule(location, generator, List.of(first, second));

        MysteryCellTeleport() {
            when(location.isActive()).thenReturn(true);
            when(location.getCurrentCellPosition()).thenReturn(MYSTERY_CELL);
        }

        @Test
        void aPieceLandingOnTheActiveMysteryCellIsTeleported() {
            Piece piece = Fixtures.placeOnTrackClockwise(red, 0, MYSTERY_CELL);

            Optional<Command> teleport = rule.findTeleport(red, piece);

            assertInstanceOf(MysteryCellTeleportCommand.class, teleport.orElseThrow());
        }

        @Test
        void nothingHappensWhenTheMysteryCellIsNotActive() {
            when(location.isActive()).thenReturn(false);
            Piece piece = Fixtures.placeOnTrackClockwise(red, 0, MYSTERY_CELL);

            assertTrue(rule.findTeleport(red, piece).isEmpty());
        }

        @Test
        void nothingHappensOnAnyOtherCell() {
            Piece piece = Fixtures.placeOnTrackClockwise(red, 0, MYSTERY_CELL + 1);

            assertTrue(rule.findTeleport(red, piece).isEmpty());
        }

        @Test
        void aPieceNotOnTheTrackIsNeverTeleported() {
            Piece atBase = red.getPieces().get(0);

            assertTrue(rule.findTeleport(red, atBase).isEmpty());
        }

        @Test
        void theRandomNumberPicksTheDestination() {
            Piece piece = Fixtures.placeOnTrackClockwise(red, 0, MYSTERY_CELL);
            GameMessagePublisher publisher = mock(GameMessagePublisher.class);

            rule.findTeleport(red, piece).orElseThrow().execute(publisher);

            verify(second).receive(red, List.of(piece), publisher);
            verify(first, never()).receive(red, List.of(piece), publisher);
        }

        @Test
        void theDestinationIsDrawnFromTheWholeListOfDestinations() {
            Piece piece = Fixtures.placeOnTrackClockwise(red, 0, MYSTERY_CELL);

            rule.findTeleport(red, piece);

            assertEquals(1, drawnRanges.size());
            assertEquals(0, drawnRanges.get(0)[0]);
            assertEquals(1, drawnRanges.get(0)[1]);
        }

        @Test
        void aWholeBlockOnTheCellIsTeleportedTogether() {
            Piece piece = Fixtures.placeOnTrackClockwise(red, 0, MYSTERY_CELL);
            Fixtures.placeOnTrackClockwise(red, 1, MYSTERY_CELL);

            Command command = rule.findTeleport(red, piece).orElseThrow();

            assertEquals(2, command.getAffectedPieces().size());
        }

        @Test
        void noRandomNumberIsDrawnWhenThereIsNoTeleport() {
            Piece piece = Fixtures.placeOnTrackClockwise(red, 0, MYSTERY_CELL + 1);

            rule.findTeleport(red, piece);

            assertTrue(drawnRanges.isEmpty());
        }
    }
}
