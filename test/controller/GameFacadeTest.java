package controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import config.enums.PlayerColor;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import message.observer.GameMessageCenter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Whole games, played from the public entry point. Only seeds known to finish are used: some seeds
// never finish (a known deadlock between blocked players), and a test must not wait on them.
@DisplayName("GameFacade (whole game)")
class GameFacadeTest {

    private static final long FINISHING_SEED = 9L;
    private static final long OTHER_FINISHING_SEED = 1L;
    private static final Duration TIME_LIMIT = Duration.ofSeconds(120);

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
        LOGGER.setUseParentHandlers(false);
    }

    @AfterEach
    void cleanUpSharedState() {
        System.setOut(originalOut);
        LOGGER.removeHandler(recordingHandler);
        LOGGER.setUseParentHandlers(true);
        GameMessageCenter.getInstance().clearObservers();
    }

    private String play(long seed) {
        return capture(() -> GameFacade.startGame(seed));
    }

    private String capture(Runnable game) {
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured, true, StandardCharsets.UTF_8));

        try {
            assertTimeoutPreemptively(TIME_LIMIT, game::run);
        } finally {
            System.setOut(originalOut);
        }

        return captured.toString(StandardCharsets.UTF_8);
    }

    @Test
    void aGamePlaysFromTheStartToTheGameOverBanner() {
        String output = play(FINISHING_SEED);

        assertTrue(output.contains("Starting the Ludo game!"));
        assertTrue(output.contains("GAME OVER!"));
        assertTrue(output.contains("FINAL STANDINGS:"));
    }

    @Test
    void theGameOverBannerComesLast() {
        String output = play(FINISHING_SEED);

        assertTrue(output.stripTrailing().endsWith("=".repeat(50)));
        assertTrue(output.lastIndexOf("GAME OVER!") > output.lastIndexOf("Round"));
    }

    @Test
    void everyPlayerIsRankedExactlyOnce() {
        String output = play(FINISHING_SEED);

        Set<PlayerColor> ranked = EnumSet.noneOf(PlayerColor.class);
        for (String place : List.of("1st", "2nd", "3rd", "4th")) {
            int start = output.indexOf(place + " Place: ") + (place + " Place: ").length();
            int end = output.indexOf('\n', start);
            ranked.add(PlayerColor.valueOf(output.substring(start, end).strip()));
        }

        assertEquals(EnumSet.allOf(PlayerColor.class), ranked);
    }

    @Test
    void everyPlayersPiecesAreAnnouncedBeforePlay() {
        String output = play(FINISHING_SEED);

        assertTrue(output.contains("The red player has four (04) pieces named R1, R2, R3, and R4."));
        assertTrue(output.contains("The yellow player has four (04) pieces named Y1, Y2, Y3, and Y4."));
        assertTrue(output.contains("The green player has four (04) pieces named G1, G2, G3, and G4."));
        assertTrue(output.contains("The blue player has four (04) pieces named B1, B2, B3, and B4."));
    }

    @Test
    void aTossDecidesWhoGoesFirst() {
        String output = play(FINISHING_SEED);

        assertTrue(output.contains("Rolling The Dice To Determine Who Goes First"));
        assertTrue(output.contains("Won The Toss With A"));
    }

    @Test
    void everyRoundIsReportedWithThePieceCounts() {
        String output = play(FINISHING_SEED);

        assertTrue(output.contains("1. Round 1"));
        assertTrue(output.contains("pieces on the board and"));
    }

    @Test
    void theSameSeedPlaysTheSameGame() {
        assertEquals(play(FINISHING_SEED), play(FINISHING_SEED));
    }

    @Test
    void aDifferentSeedPlaysADifferentGame() {
        assertNotEquals(play(FINISHING_SEED), play(OTHER_FINISHING_SEED));
    }

    @Test
    void aFinishedGameLogsNoFailure() {
        play(FINISHING_SEED);

        assertTrue(loggedRecords.stream().noneMatch(record -> record.getLevel().intValue() >= Level.SEVERE.intValue()),
                "a SEVERE record means the game aborted");
    }

    @Test
    void theProgramEntryPointPlaysTheSameGameAsSeedNine() {
        String viaMain = capture(GameFacadeTest::runMainMethod);

        assertEquals(play(FINISHING_SEED), viaMain);
    }

    // Main sits in the default package, so it cannot be imported and is reached by name.
    private static void runMainMethod() {
        try {
            Class.forName("Main").getMethod("main", String[].class).invoke(null, (Object) new String[0]);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Main.main could not be run", exception);
        }
    }
}
