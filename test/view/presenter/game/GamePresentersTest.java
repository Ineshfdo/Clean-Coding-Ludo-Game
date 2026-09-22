package view.presenter.game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import config.enums.PlayerColor;
import java.util.List;
import message.game.GameOver;
import message.game.GameStarting;
import message.game.PlayerRosterAnnounced;
import message.game.RoundStarted;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Game presenters")
class GamePresentersTest {

    private static final String BORDER = "=".repeat(50);

    @Test
    void gameStartingAnnouncesTheStart() {
        assertEquals("\nStarting the Ludo game!\n", new GameStartingPresenter().present(new GameStarting()));
    }

    @Test
    void roundStartedNumbersTheRound() {
        assertEquals("\n\n7. Round 7", new RoundStartedPresenter().present(new RoundStarted(7)));
    }

    @Test
    void rosterListsFourPiecesInWords() {
        String text = new PlayerRosterAnnouncedPresenter()
                .present(new PlayerRosterAnnounced(PlayerColor.RED, List.of("R1", "R2", "R3", "R4")));

        assertEquals("The red player has four (04) pieces named R1, R2, R3, and R4.", text);
    }

    @Test
    void rosterWithTwoPiecesUsesDigits() {
        String text = new PlayerRosterAnnouncedPresenter()
                .present(new PlayerRosterAnnounced(PlayerColor.BLUE, List.of("B1", "B2")));

        assertEquals("The blue player has 2 (02) pieces named B1, and B2.", text);
    }

    @Test
    void rosterWithOnePieceNamesItAlone() {
        String text = new PlayerRosterAnnouncedPresenter()
                .present(new PlayerRosterAnnounced(PlayerColor.GREEN, List.of("G1")));

        assertEquals("The green player has 1 (01) pieces named G1.", text);
    }

    @Test
    void gameOverListsEveryPlayerInFinishingOrder() {
        String text = new GameOverPresenter().present(
                new GameOver(List.of(PlayerColor.GREEN, PlayerColor.RED, PlayerColor.BLUE, PlayerColor.YELLOW)));

        assertEquals("\n" + BORDER + "\n"
                + "                   GAME OVER!\n"
                + BORDER + "\n"
                + "FINAL STANDINGS:\n"
                + "1st Place: GREEN\n2nd Place: RED\n3rd Place: BLUE\n4th Place: YELLOW\n"
                + BORDER, text);
    }

    @Test
    void gameOverWithOneFinisherListsOnlyFirstPlace() {
        String text = new GameOverPresenter().present(new GameOver(List.of(PlayerColor.RED)));

        assertEquals("\n" + BORDER + "\n"
                + "                   GAME OVER!\n"
                + BORDER + "\n"
                + "FINAL STANDINGS:\n"
                + "1st Place: RED\n"
                + BORDER, text);
    }
}
