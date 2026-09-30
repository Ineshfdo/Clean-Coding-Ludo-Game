package view.presenter.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import config.enums.PlayerColor;
import java.util.List;
import message.game.BoardStateReported;
import model.player.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.Fixtures;

// This report reads the live players, so each test builds the situation it wants to see described.
@DisplayName("BoardStateReportedPresenter")
class BoardStateReportedPresenterTest {

    private static final String SHORT_BORDER = "-".repeat(31);

    private final Player red = Fixtures.playerOf(PlayerColor.RED);
    private final Player green = Fixtures.playerOf(PlayerColor.GREEN);
    private final BoardStateReportedPresenter presenter = new BoardStateReportedPresenter(List.of(red));

    private String report() {
        return presenter.present(new BoardStateReported(3));
    }

    @Test
    void aPlayerWithEverythingAtBaseGetsTheSummary() {
        assertEquals("\n" + SHORT_BORDER + "\n"
                + "Red player now has 0/4 pieces on the board and 4/4 pieces on the base.\n"
                + SHORT_BORDER + "\n", report());
    }

    @Test
    void theSummaryCountsPiecesOnTheBoardAndAtBase() {
        Fixtures.placeOnTrackClockwise(red, 0, 10);
        Fixtures.placeOnTrackClockwise(red, 1, 20);

        assertTrue(report().contains("Red player now has 2/4 pieces on the board and 2/4 pieces on the base."));
    }

    @Test
    void theReportHasNoBoardStateDump() {
        Fixtures.placeOnTrackClockwise(red, 0, 12);

        assertFalse(report().contains("Current Board State"));
        assertFalse(report().contains("R1("));
    }

    @Test
    void playersAreListedInThePlayOrderTheyWereGiven() {
        BoardStateReportedPresenter twoPlayers = new BoardStateReportedPresenter(List.of(red, green));

        String text = twoPlayers.present(new BoardStateReported(1));

        assertTrue(text.indexOf("Red player") < text.indexOf("Green player"));
    }

    @Test
    void setTurnOrderChangesTheOrderOfTheLines() {
        BoardStateReportedPresenter twoPlayers = new BoardStateReportedPresenter(List.of(red, green));

        twoPlayers.setTurnOrder(List.of(green, red));
        String text = twoPlayers.present(new BoardStateReported(1));

        assertTrue(text.indexOf("Green player") < text.indexOf("Red player"));
    }
}
