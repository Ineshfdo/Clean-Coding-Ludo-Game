package utils.coin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import config.enums.CoinTossResult;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.randomgenerator.SeededRandomNumberGenerator;

@DisplayName("SeededCoinToss")
class SeededCoinTossTest {

    private static final long SEED = 11L;
    private static final int FLIPS = 100;

    private final CoinToss coinToss = SeededCoinToss.getInstance();

    @BeforeEach
    void seedTheSharedGenerator() {
        SeededRandomNumberGenerator.getInstance().setSeed(SEED);
    }

    @Test
    void getInstanceAlwaysReturnsTheSameCoin() {
        assertSame(coinToss, SeededCoinToss.getInstance());
    }

    @Test
    void bothHeadsAndTailsCanCome() {
        Set<CoinTossResult> seen = EnumSet.noneOf(CoinTossResult.class);
        seen.addAll(flipMany(FLIPS));

        assertEquals(EnumSet.allOf(CoinTossResult.class), seen);
    }

    @Test
    void sameSeedRepeatsTheSameFlips() {
        List<CoinTossResult> first = flipMany(FLIPS);

        SeededRandomNumberGenerator.getInstance().setSeed(SEED);
        List<CoinTossResult> second = flipMany(FLIPS);

        assertEquals(first, second);
    }

    private List<CoinTossResult> flipMany(int count) {
        List<CoinTossResult> results = new ArrayList<>();

        for (int index = 0; index < count; index++) {
            results.add(coinToss.flip());
        }

        return results;
    }
}
