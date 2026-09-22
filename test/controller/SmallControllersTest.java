package controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import config.enums.PlayerColor;
import exception.PlayerNotFoundException;
import java.util.ArrayList;
import java.util.List;
import message.observer.GameMessagePublisher;
import message.toss.DiceRolled;
import message.toss.TossStarting;
import message.toss.TossTied;
import message.toss.TossWon;
import message.turn.HomeGateOpened;
import model.board.LudoBoard;
import model.player.Player;
import model.player.rule.roll.RollEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import support.Fixtures;
import utils.dice.Dice;

@DisplayName("Small controllers")
class SmallControllersTest {

    private final Player red = Fixtures.playerOf(PlayerColor.RED);
    private final Player yellow = Fixtures.playerOf(PlayerColor.YELLOW);
    private final Player green = Fixtures.playerOf(PlayerColor.GREEN);
    private final Player blue = Fixtures.playerOf(PlayerColor.BLUE);
    private final List<Player> everyone = List.of(red, yellow, green, blue);
    private final GameMessagePublisher publisher = mock(GameMessagePublisher.class);

    @Nested
    @DisplayName("RoundTracker")
    class Rounds {

        private final RoundTracker tracker = new RoundTracker(everyone);

        @Test
        void startsBeforeTheFirstRound() {
            assertEquals(0, tracker.getRoundNumber());
        }

        @Test
        void startNextRoundCountsUpFromOne() {
            assertEquals(1, tracker.startNextRound());
            assertEquals(2, tracker.startNextRound());
            assertEquals(3, tracker.startNextRound());
        }

        @Test
        void theRoundNumberFollowsTheLatestStartedRound() {
            tracker.startNextRound();
            tracker.startNextRound();

            assertEquals(2, tracker.getRoundNumber());
        }

        @Test
        void keepsTheTurnOrderItWasGiven() {
            assertEquals(everyone, tracker.getTurnOrder());
        }

        @Test
        void theTurnOrderIsACopyOfTheGivenList() {
            List<Player> changeable = new ArrayList<>(everyone);
            RoundTracker copyTracker = new RoundTracker(changeable);

            changeable.clear();

            assertEquals(4, copyTracker.getTurnOrder().size());
        }

        @Test
        void theTurnOrderCannotBeChangedFromOutside() {
            List<Player> order = tracker.getTurnOrder();

            assertThrows(UnsupportedOperationException.class, () -> order.remove(0));
        }
    }

    @Nested
    @DisplayName("TurnOrderBuilder")
    class TurnOrder {

        private final TurnOrderBuilder builder = new TurnOrderBuilder(LudoBoard.getInstance());

        @Test
        void turnOrderRunsClockwiseFromTheStartingColor() {
            assertEquals(List.of(red, green, yellow, blue), builder.buildFrom(PlayerColor.RED, everyone));
        }

        @Test
        void aDifferentStartingColorRotatesTheOrder() {
            assertEquals(List.of(yellow, blue, red, green), builder.buildFrom(PlayerColor.YELLOW, everyone));
        }

        @Test
        void theOrderDoesNotDependOnTheOrderTheListWasGivenIn() {
            List<Player> shuffled = List.of(blue, green, red, yellow);

            assertEquals(List.of(green, yellow, blue, red), builder.buildFrom(PlayerColor.GREEN, shuffled));
        }

        @Test
        void everyPlayerAppearsExactlyOnce() {
            assertEquals(4, builder.buildFrom(PlayerColor.BLUE, everyone).stream().distinct().count());
        }

        @Test
        void aMissingColorIsAnError() {
            List<Player> missingGreen = List.of(red, yellow, blue);

            assertThrows(PlayerNotFoundException.class, () -> builder.buildFrom(PlayerColor.RED, missingGreen));
        }

        @Test
        void theErrorNamesTheMissingColor() {
            List<Player> onlyRedAndYellow = List.of(red, yellow);

            PlayerNotFoundException exception = assertThrows(
                    PlayerNotFoundException.class, () -> builder.buildFrom(PlayerColor.RED, onlyRedAndYellow));

            assertEquals("No player with color GREEN", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("FirstPlayerSelector")
    class FirstPlayer {

        private final Dice dice = mock(Dice.class);
        private final FirstPlayerSelector selector = new FirstPlayerSelector(dice, publisher);

        @Test
        void theHighestRollerGoesFirst() {
            when(dice.roll()).thenReturn(3, 5, 2, 4);

            assertSame(yellow, selector.select(List.of(red, yellow, green, blue)));
        }

        @Test
        void everyPlayerRollsOnce() {
            when(dice.roll()).thenReturn(3, 5, 2, 4);

            selector.select(List.of(red, yellow, green, blue));

            verify(publisher).publish(new DiceRolled(PlayerColor.RED, 3));
            verify(publisher).publish(new DiceRolled(PlayerColor.YELLOW, 5));
            verify(publisher).publish(new DiceRolled(PlayerColor.GREEN, 2));
            verify(publisher).publish(new DiceRolled(PlayerColor.BLUE, 4));
        }

        @Test
        void theTossIsAnnouncedBeforeTheRollsAndTheWinnerAfterThem() {
            when(dice.roll()).thenReturn(1, 6, 2, 3);

            selector.select(List.of(red, yellow, green, blue));

            InOrder order = inOrder(publisher);
            order.verify(publisher).publish(new TossStarting());
            order.verify(publisher).publish(new DiceRolled(PlayerColor.RED, 1));
            order.verify(publisher).publish(new TossWon(PlayerColor.YELLOW, 6));
        }

        @Test
        void aTieForTheHighestRollMakesEveryoneRollAgain() {
            when(dice.roll()).thenReturn(6, 6, 2, 3, 1, 2, 3, 6);

            Player winner = selector.select(List.of(red, yellow, green, blue));

            assertSame(blue, winner);
            verify(publisher).publish(new TossTied(6));
        }

        @Test
        void aTieBelowTheHighestRollDoesNotMatter() {
            when(dice.roll()).thenReturn(3, 3, 5, 1);

            Player winner = selector.select(List.of(red, yellow, green, blue));

            assertSame(green, winner);
            verify(publisher, never()).publish(new TossTied(3));
        }

        @Test
        void aRerollUsesTheDiceForEveryPlayerAgain() {
            when(dice.roll()).thenReturn(4, 4, 4, 4, 1, 2, 3, 5);

            selector.select(List.of(red, yellow, green, blue));

            verify(dice, times(8)).roll();
        }
    }

    @Nested
    @DisplayName("HomeGateTracker")
    class HomeGate {

        private final HomeGateTracker tracker = new HomeGateTracker();

        private void rollByRed() {
            tracker.onRollAccepted(new RollEvent(red, everyone, 1, 4), publisher);
        }

        private void everyOpponentOfRedIsHome() {
            Fixtures.sendEveryPieceHome(yellow);
            Fixtures.sendEveryPieceHome(green);
            Fixtures.sendEveryPieceHome(blue);
        }

        @Test
        void theGateIsClosedAtTheStart() {
            for (PlayerColor color : PlayerColor.values()) {
                assertFalse(tracker.isOpenFor(color));
            }
        }

        @Test
        void rollsWhileOpponentsAreStillPlayingNeverOpenTheGate() {
            for (int roll = 0; roll < 5; roll++) {
                rollByRed();
            }

            assertFalse(tracker.isOpenFor(PlayerColor.RED));
        }

        @Test
        void threeRollsWithEveryOpponentHomeOpenTheGate() {
            everyOpponentOfRedIsHome();

            rollByRed();
            rollByRed();
            assertFalse(tracker.isOpenFor(PlayerColor.RED));
            rollByRed();

            assertTrue(tracker.isOpenFor(PlayerColor.RED));
        }

        @Test
        void theGateOpeningIsAnnouncedOnTheRollThatOpensIt() {
            everyOpponentOfRedIsHome();

            rollByRed();
            rollByRed();
            verifyNoInteractions(publisher);
            rollByRed();

            verify(publisher).publish(new HomeGateOpened(PlayerColor.RED));
        }

        @Test
        void theOpeningIsAnnouncedOnlyOnce() {
            everyOpponentOfRedIsHome();

            for (int roll = 0; roll < 6; roll++) {
                rollByRed();
            }

            verify(publisher, times(1)).publish(new HomeGateOpened(PlayerColor.RED));
        }

        @Test
        void theGateStaysOpenAfterFurtherRolls() {
            everyOpponentOfRedIsHome();

            for (int roll = 0; roll < 6; roll++) {
                rollByRed();
            }

            assertTrue(tracker.isOpenFor(PlayerColor.RED));
        }

        @Test
        void anOpponentStillPlayingResetsTheCount() {
            everyOpponentOfRedIsHome();
            rollByRed();
            rollByRed();
            blue.getPieces().get(0).returnToBase();

            rollByRed();

            assertFalse(tracker.isOpenFor(PlayerColor.RED));
        }

        @Test
        void theGateIsCountedSeparatelyForEachColor() {
            everyOpponentOfRedIsHome();

            for (int roll = 0; roll < 3; roll++) {
                rollByRed();
            }

            assertFalse(tracker.isOpenFor(PlayerColor.GREEN));
        }
    }
}
