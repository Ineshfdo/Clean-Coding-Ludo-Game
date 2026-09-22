package utils.dice;

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
import utils.randomgenerator.SeededRandomNumberGenerator;

@DisplayName("SixSidedDice")
class SixSidedDiceTest {

    private static final long SEED = 7L;
    private static final int ROLLS = 300;

    private final Dice dice = SixSidedDice.getInstance();

    @BeforeEach
    void seedTheSharedGenerator() {
        SeededRandomNumberGenerator.getInstance().setSeed(SEED);
    }

    @Test
    void getInstanceAlwaysReturnsTheSameDie() {
        assertSame(dice, SixSidedDice.getInstance());
    }

    @Test
    void everyRollIsBetweenOneAndSix() {
        for (int roll : rollMany(ROLLS)) {
            assertTrue(roll >= 1 && roll <= 6, "rolled " + roll);
        }
    }

    @Test
    void allSixFacesAppear() {
        Set<Integer> faces = new TreeSet<>(rollMany(ROLLS));

        assertEquals(Set.of(1, 2, 3, 4, 5, 6), faces);
    }

    @Test
    void sameSeedRepeatsTheSameRolls() {
        List<Integer> first = rollMany(50);

        SeededRandomNumberGenerator.getInstance().setSeed(SEED);
        List<Integer> second = rollMany(50);

        assertEquals(first, second);
    }

    private List<Integer> rollMany(int count) {
        List<Integer> rolls = new ArrayList<>();

        for (int index = 0; index < count; index++) {
            rolls.add(dice.roll());
        }

        return rolls;
    }
}
