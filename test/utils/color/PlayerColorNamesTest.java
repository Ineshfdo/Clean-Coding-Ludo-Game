package utils.color;

import static org.junit.jupiter.api.Assertions.assertEquals;

import config.enums.PlayerColor;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("PlayerColorNames")
class PlayerColorNamesTest {

    @ParameterizedTest(name = "{0} is shown as {1}")
    @CsvSource({"RED,Red", "YELLOW,Yellow", "GREEN,Green", "BLUE,Blue"})
    void displayNameCapitalisesOnlyTheFirstLetter(PlayerColor color, String expectedName) {
        assertEquals(expectedName, PlayerColorNames.displayNameOf(color));
    }

    @ParameterizedTest(name = "{0} has the short code {1}")
    @CsvSource({"RED,R", "YELLOW,Y", "GREEN,G", "BLUE,B"})
    void shortCodeIsTheFirstLetterOfTheColor(PlayerColor color, String expectedCode) {
        assertEquals(expectedCode, PlayerColorNames.shortCodeOf(color));
    }

    @Test
    void everyColorHasADistinctShortCode() {
        long distinctCodes = Arrays.stream(PlayerColor.values())
                .map(PlayerColorNames::shortCodeOf)
                .distinct()
                .count();

        assertEquals(PlayerColor.values().length, distinctCodes);
    }
}
