package model.effect.mysterycell;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import config.enums.PlayerColor;
import java.util.List;
import message.observer.GameMessagePublisher;
import model.board.LudoBoard;
import model.player.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.Fixtures;

// The two-round wait starts from the first round that finishes with a piece on the track.
@DisplayName("MysteryCellSchedule first entry")
class MysteryCellScheduleFirstEntryTest {

    private final Player red = Fixtures.playerOf(PlayerColor.RED);
    private final List<Player> players = List.of(red);
    private final GameMessagePublisher publisher = mock(GameMessagePublisher.class);
    private final MysteryCellSchedule schedule = new MysteryCellSchedule(LudoBoard.getInstance(), (min, max) -> min);

    @Test
    void aRoundEndingWithNobodyOnTheTrackStartsNoCountdown() {
        schedule.onRoundCompleted(1, players);
        Fixtures.placeOnTrackClockwise(red, 0, 28);

        schedule.onRoundStarted(3, players, publisher);

        assertFalse(schedule.isActive());
    }

    @Test
    void theCountdownStartsOnTheFirstRoundThatEndsWithAPieceOnTheTrack() {
        schedule.onRoundCompleted(1, players);
        Fixtures.placeOnTrackClockwise(red, 0, 28);
        schedule.onRoundCompleted(2, players);

        schedule.onRoundStarted(4, players, publisher);

        assertTrue(schedule.isActive());
    }
}
