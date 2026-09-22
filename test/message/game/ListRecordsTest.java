package message.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import config.enums.PlayerColor;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// The two records that carry a list copy it, so later changes to the caller's list cannot alter an event.
@DisplayName("Message records that hold lists")
class ListRecordsTest {

    @Test
    void gameOverKeepsTheStandingsItWasGiven() {
        List<PlayerColor> standings = new ArrayList<>(List.of(PlayerColor.GREEN, PlayerColor.RED));
        GameOver gameOver = new GameOver(standings);

        standings.add(PlayerColor.BLUE);

        assertEquals(List.of(PlayerColor.GREEN, PlayerColor.RED), gameOver.finalStandings());
    }

    @Test
    void gameOverStandingsCannotBeModified() {
        GameOver gameOver = new GameOver(List.of(PlayerColor.RED));

        assertThrows(UnsupportedOperationException.class,
                () -> gameOver.finalStandings().add(PlayerColor.BLUE));
    }

    @Test
    void rosterKeepsThePieceLabelsItWasGiven() {
        List<String> labels = new ArrayList<>(List.of("R1", "R2"));
        PlayerRosterAnnounced roster = new PlayerRosterAnnounced(PlayerColor.RED, labels);

        labels.clear();

        assertEquals(List.of("R1", "R2"), roster.pieceLabels());
    }

    @Test
    void rosterPieceLabelsCannotBeModified() {
        PlayerRosterAnnounced roster = new PlayerRosterAnnounced(PlayerColor.RED, List.of("R1"));

        assertThrows(UnsupportedOperationException.class, () -> roster.pieceLabels().add("R2"));
    }

    @Test
    void recordsWithTheSameContentAreEqual() {
        assertEquals(new GameOver(List.of(PlayerColor.RED)), new GameOver(List.of(PlayerColor.RED)));
    }

    @Test
    void recordsWithDifferentContentAreNotEqual() {
        assertNotEquals(new GameOver(List.of(PlayerColor.RED)), new GameOver(List.of(PlayerColor.BLUE)));
    }
}
