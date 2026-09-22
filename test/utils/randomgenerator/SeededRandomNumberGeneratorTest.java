package utils.randomgenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SeededRandomNumberGenerator")
class SeededRandomNumberGeneratorTest {

    private static final long SEED = 42L;
    private static final int SAMPLE_SIZE = 200;

    private final SeededRandomNumberGenerator generator = SeededRandomNumberGenerator.getInstance();

    // The generator is shared by the whole game, so each test starts from a known seed.
    @BeforeEach
    void seedGenerator() {
        generator.setSeed(SEED);
    }

    @Test
    void getInstanceAlwaysReturnsTheSameGenerator() {
        assertSame(generator, SeededRandomNumberGenerator.getInstance());
    }

    @Test
    void sameSeedGivesSameSequence() {
        List<Integer> first = draw(SAMPLE_SIZE, 1, 6);

        generator.setSeed(SEED);
        List<Integer> second = draw(SAMPLE_SIZE, 1, 6);

        assertEquals(first, second);
    }

    @Test
    void everyDrawStaysInsideTheInclusiveRange() {
        for (int drawn : draw(SAMPLE_SIZE, 3, 8)) {
            assertTrue(drawn >= 3 && drawn <= 8, "drawn " + drawn);
        }
    }

    @Test
    void bothEndsOfTheRangeCanBeDrawn() {
        Set<Integer> drawnValues = new TreeSet<>(draw(SAMPLE_SIZE, 1, 3));

        assertEquals(Set.of(1, 2, 3), drawnValues);
    }

    @Test
    void singleValueRangeAlwaysReturnsThatValue() {
        for (int drawn : draw(20, 5, 5)) {
            assertEquals(5, drawn);
        }
    }

    @Test
    void negativeRangeIsSupported() {
        for (int drawn : draw(SAMPLE_SIZE, -2, 2)) {
            assertTrue(drawn >= -2 && drawn <= 2, "drawn " + drawn);
        }
    }

    private List<Integer> draw(int count, int minInclusive, int maxInclusive) {
        List<Integer> drawnValues = new ArrayList<>();

        for (int index = 0; index < count; index++) {
            drawnValues.add(generator.nextIntInRange(minInclusive, maxInclusive));
        }

        return drawnValues;
    }
}
