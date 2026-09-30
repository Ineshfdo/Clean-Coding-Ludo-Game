package view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import config.enums.PlayerColor;
import exception.UnpresentableMessageException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import message.GameMessage;
import message.game.BoardStateReported;
import message.game.GameStarting;
import message.toss.TossWon;
import model.board.LudoBoard;
import model.player.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.Fixtures;
import view.presenter.EventPresenter;

@DisplayName("Console view")
class ConsoleViewTest {

    private static final String NEW_LINE = System.lineSeparator();

    private final Player red = Fixtures.playerOf(PlayerColor.RED);
    private final Player green = Fixtures.playerOf(PlayerColor.GREEN);
    private final List<Player> everyone = List.of(red, green);

    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream captured = new ByteArrayOutputStream();

    @BeforeEach
    void captureConsole() {
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreConsole() {
        System.setOut(originalOut);
    }

    private String printed() {
        return captured.toString(StandardCharsets.UTF_8);
    }

    private ConsoleGameObserver newObserver() {
        return new ConsoleGameObserver(everyone, LudoBoard.getInstance());
    }

    @Nested
    @DisplayName("ConsoleGameObserver")
    class Observer {

        @Test
        void aMessageIsPrintedAsItsPresentedTextOnOneLine() {
            newObserver().onGameMessage(new GameStarting());

            assertEquals("\nStarting the Ludo game!\n" + NEW_LINE, printed());
        }

        @Test
        void eachMessageIsPrintedInTheOrderItArrives() {
            ConsoleGameObserver observer = newObserver();

            observer.onGameMessage(new TossWon(PlayerColor.RED, 6));
            observer.onGameMessage(new GameStarting());

            assertTrue(printed().indexOf("Won The Toss") < printed().indexOf("Starting the Ludo game"));
        }

        @Test
        void aMessageWithNoPresenterFailsInsteadOfPrintingNothing() {
            ConsoleGameObserver observer = newObserver();
            GameMessage unknown = new GameMessage() {
            };

            assertThrows(UnpresentableMessageException.class, () -> observer.onGameMessage(unknown));
        }

        @Test
        void theRoundReportListsPlayersInTheOrderTheyWereGiven() {
            newObserver().onGameMessage(new BoardStateReported(1));

            assertTrue(printed().indexOf("Red player") < printed().indexOf("Green player"));
        }

        @Test
        void setTurnOrderChangesTheOrderOfTheRoundReport() {
            ConsoleGameObserver observer = newObserver();

            observer.setTurnOrder(List.of(green, red));
            observer.onGameMessage(new BoardStateReported(1));

            assertTrue(printed().indexOf("Green player") < printed().indexOf("Red player"));
        }
    }

    @Nested
    @DisplayName("PresenterCatalog")
    class Catalog {

        private final PresenterCatalog catalog = new PresenterCatalog(everyone, LudoBoard.getInstance());

        @Test
        void listsOnePresenterForEachOfTheThirtyFiveEvents() {
            assertEquals(35, catalog.getPresenters().size());
        }

        @Test
        void noTwoPresentersHandleTheSameEvent() {
            Set<Class<?>> handledEvents = new HashSet<>();

            for (EventPresenter<? extends GameMessage> presenter : catalog.getPresenters()) {
                assertTrue(handledEvents.add(presenter.getEventType()), presenter.getEventType().getSimpleName());
            }
        }

        @Test
        void setTurnOrderReachesTheBoardStatePresenter() {
            catalog.setTurnOrder(List.of(green, red));

            EventPresenter<? extends GameMessage> boardState = catalog.getPresenters().stream()
                    .filter(presenter -> presenter.getEventType() == BoardStateReported.class)
                    .findFirst()
                    .orElseThrow();
            String text = presentWith(boardState);

            assertTrue(text.indexOf("Green player") < text.indexOf("Red player"));
        }

        @SuppressWarnings("unchecked")
        private String presentWith(EventPresenter<? extends GameMessage> presenter) {
            return ((EventPresenter<BoardStateReported>) presenter).present(new BoardStateReported(1));
        }
    }
}
