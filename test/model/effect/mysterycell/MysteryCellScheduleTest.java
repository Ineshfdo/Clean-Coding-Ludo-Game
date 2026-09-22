package model.effect.mysterycell;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import config.enums.PlayerColor;
import java.util.List;
import message.mystery.MysteryCellAppeared;
import message.mystery.MysteryCellRelocated;
import message.observer.GameMessagePublisher;
import model.board.LudoBoard;
import model.player.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.Fixtures;
import utils.randomgenerator.RandomNumberGenerator;

@DisplayName("MysteryCellSchedule")
class MysteryCellScheduleTest {

    // Always draws the lowest allowed index, so the chosen cell is the first empty one.
    private static final RandomNumberGenerator ALWAYS_FIRST = (min, max) -> min;

    private final Player red = Fixtures.playerOf(PlayerColor.RED);
    private final List<Player> players = List.of(red);
    private final GameMessagePublisher publisher = mock(GameMessagePublisher.class);
    private final MysteryCellSchedule schedule = new MysteryCellSchedule(LudoBoard.getInstance(), ALWAYS_FIRST);

    @Test
    void startsInactiveWithNoCell() {
        assertFalse(schedule.isActive());
        assertEquals(-1, schedule.getCurrentCellPosition());
    }

    @Test
    void doesNotAppearBeforeAnyPieceHasEnteredTheTrack() {
        schedule.onRoundStarted(10, players, publisher);

        assertFalse(schedule.isActive());
        verifyNoInteractions(publisher);
    }

    @Test
    void doesNotAppearTheRoundAfterTheFirstEntry() {
        Fixtures.placeOnTrackClockwise(red, 0, 28);
        schedule.onRoundCompleted(1, players);

        schedule.onRoundStarted(2, players, publisher);

        assertFalse(schedule.isActive());
    }

    @Test
    void appearsTwoRoundsAfterTheFirstPieceEntersTheTrack() {
        Fixtures.placeOnTrackClockwise(red, 0, 28);
        schedule.onRoundCompleted(1, players);

        schedule.onRoundStarted(3, players, publisher);

        assertTrue(schedule.isActive());
    }

    @Test
    void appearsOnTheFirstEmptyCellWhenTheDrawIsZero() {
        Fixtures.placeOnTrackClockwise(red, 0, 28);
        schedule.onRoundCompleted(1, players);

        schedule.onRoundStarted(3, players, publisher);

        assertEquals(0, schedule.getCurrentCellPosition());
        verify(publisher).publish(new MysteryCellAppeared(0));
    }

    @Test
    void neverAppearsOnAnOccupiedCell() {
        Fixtures.placeOnTrackClockwise(red, 0, 0);
        schedule.onRoundCompleted(1, players);

        schedule.onRoundStarted(3, players, publisher);

        assertEquals(1, schedule.getCurrentCellPosition());
    }

    @Test
    void onRoundCompletedRecordsOnlyTheFirstEntryRound() {
        Fixtures.placeOnTrackClockwise(red, 0, 28);
        schedule.onRoundCompleted(1, players);
        schedule.onRoundCompleted(2, players);

        // Two rounds after round 1 (not round 2), so it appears at round 3.
        schedule.onRoundStarted(3, players, publisher);

        assertTrue(schedule.isActive());
    }

    @Test
    void doesNotAppearWhenNoPieceIsOnTheTrackAnyMore() {
        Fixtures.placeOnTrackClockwise(red, 0, 28);
        schedule.onRoundCompleted(1, players);
        red.getPieces().get(0).returnToBase();

        schedule.onRoundStarted(3, players, publisher);

        assertFalse(schedule.isActive());
    }

    @Test
    void staysOnTheSameCellForFourRounds() {
        appearAtRound3();

        schedule.onRoundStarted(4, players, publisher);
        schedule.onRoundStarted(5, players, publisher);
        schedule.onRoundStarted(6, players, publisher);

        assertEquals(0, schedule.getCurrentCellPosition());
        verify(publisher, never()).publish(any(MysteryCellRelocated.class));
    }

    @Test
    void relocatesAfterFourRoundsOnTheSameCell() {
        appearAtRound3();

        for (int round = 4; round <= 7; round++) {
            schedule.onRoundStarted(round, players, publisher);
        }

        verify(publisher).publish(new MysteryCellRelocated(1));
    }

    @Test
    void neverRelocatesOntoTheCellItJustLeft() {
        appearAtRound3();
        int previousCell = schedule.getCurrentCellPosition();

        for (int round = 4; round <= 7; round++) {
            schedule.onRoundStarted(round, players, publisher);
        }

        assertNotEquals(previousCell, schedule.getCurrentCellPosition());
    }

    @Test
    void staysActiveAfterRelocating() {
        appearAtRound3();

        for (int round = 4; round <= 7; round++) {
            schedule.onRoundStarted(round, players, publisher);
        }

        assertTrue(schedule.isActive());
    }

    private void appearAtRound3() {
        Fixtures.placeOnTrackClockwise(red, 0, 28);
        schedule.onRoundCompleted(1, players);
        schedule.onRoundStarted(3, players, publisher);
    }
}
