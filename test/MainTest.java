import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Main has no explicit constructor and no fields; this only exists to exercise the compiler-
// generated default one. Main.main() itself is exercised in GameFacadeTest, which checks it
// plays the same game as GameFacade.startGame(9L).
@DisplayName("Main")
class MainTest {

    @Test
    void hasNoBehaviorOfItsOwnBeyondTheDefaultConstructor() {
        assertDoesNotThrow(Main::new);
    }
}
