package controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import exception.IllegalMoveException;
import exception.InvalidPieceStateException;
import exception.PieceOwnershipException;
import exception.PlayerNotFoundException;
import exception.UnpresentableMessageException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Stream;
import message.observer.GameMessageCenter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

// The game has no failure injection point, so the console is made to fail: it throws as soon as
// the first line is printed, which reaches startGame's single try/catch through the real game code.
@DisplayName("GameFacade failure handling")
class GameFacadeFailureTest {

    private static final long ANY_SEED = 9L;

    private static final Logger LOGGER = Logger.getLogger(GameFacade.class.getName());

    private final PrintStream originalOut = System.out;
    private final List<LogRecord> loggedRecords = new ArrayList<>();
    private final Handler recordingHandler = new Handler() {
        @Override
        public void publish(LogRecord record) {
            loggedRecords.add(record);
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }
    };

    @BeforeEach
    void listenForLoggedFailures() {
        LOGGER.addHandler(recordingHandler);
        // These tests deliberately trigger SEVERE records; the default console handler would
        // otherwise print a full stack trace for each one, on top of the assertions below.
        LOGGER.setUseParentHandlers(false);
    }

    @AfterEach
    void cleanUpSharedState() {
        System.setOut(originalOut);
        LOGGER.removeHandler(recordingHandler);
        LOGGER.setUseParentHandlers(true);
        GameMessageCenter.getInstance().clearObservers();
    }

    static Stream<Arguments> exceptionsTheGameHandles() {
        return Stream.of(
                Arguments.of("IllegalMoveException", (Supplier<RuntimeException>) () -> new IllegalMoveException("forced")),
                Arguments.of("InvalidPieceStateException",
                        (Supplier<RuntimeException>) () -> new InvalidPieceStateException("forced")),
                Arguments.of("PieceOwnershipException",
                        (Supplier<RuntimeException>) () -> new PieceOwnershipException("forced")),
                Arguments.of("PlayerNotFoundException",
                        (Supplier<RuntimeException>) () -> new PlayerNotFoundException("forced")),
                Arguments.of("UnpresentableMessageException",
                        (Supplier<RuntimeException>) () -> new UnpresentableMessageException("forced")));
    }

    // Every line the game prints throws the given exception instead.
    private void makeTheConsoleFailWith(RuntimeException failure) {
        System.setOut(new PrintStream(new ByteArrayOutputStream()) {
            @Override
            public void println(String text) {
                throw failure;
            }
        });
    }

    @ParameterizedTest(name = "{0} does not escape startGame")
    @MethodSource("exceptionsTheGameHandles")
    void aGameFailureIsHandledInsteadOfEscaping(String name, Supplier<RuntimeException> failure) {
        makeTheConsoleFailWith(failure.get());

        assertDoesNotThrow(() -> GameFacade.startGame(ANY_SEED));
    }

    @ParameterizedTest(name = "{0} is logged as a SEVERE failure with its cause")
    @MethodSource("exceptionsTheGameHandles")
    void aGameFailureIsLoggedAsSevereWithTheOriginalException(String name, Supplier<RuntimeException> failure) {
        RuntimeException thrown = failure.get();
        makeTheConsoleFailWith(thrown);

        GameFacade.startGame(ANY_SEED);

        assertEquals(1, loggedRecords.size());
        assertEquals(Level.SEVERE, loggedRecords.get(0).getLevel());
        assertEquals("Game aborted; the simulation cannot continue", loggedRecords.get(0).getMessage());
        assertSame(thrown, loggedRecords.get(0).getThrown());
    }

    @Test
    void anExceptionThatIsNotOneOfTheGamesOwnIsNotHiddenByTheHandler() {
        makeTheConsoleFailWith(new IllegalStateException("a genuine bug"));

        IllegalStateException escaped =
                assertThrows(IllegalStateException.class, () -> GameFacade.startGame(ANY_SEED));

        assertEquals("a genuine bug", escaped.getMessage());
    }

    @Test
    void aFailureThatIsNotOneOfTheGamesOwnIsNotLogged() {
        makeTheConsoleFailWith(new IllegalStateException("a genuine bug"));

        IllegalStateException escaped =
                assertThrows(IllegalStateException.class, () -> GameFacade.startGame(ANY_SEED));

        assertEquals("a genuine bug", escaped.getMessage());
        assertEquals(0, loggedRecords.size());
    }
}
