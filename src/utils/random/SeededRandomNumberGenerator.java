package utils.random;

import java.util.Random;

// Singleton: every component needing randomness draws from this
// same seeded sequence for reproducibility.
public final class SeededRandomNumberGenerator implements SeedableRandomNumberGenerator {

    private static final SeededRandomNumberGenerator SHARED_INSTANCE =
            new SeededRandomNumberGenerator();

    private final Random randomSource;

    private SeededRandomNumberGenerator() {
        this.randomSource = new Random();
    }

    public static SeededRandomNumberGenerator getInstance() {
        return SHARED_INSTANCE;
    }

    @Override
    public void setSeed(long seed) {
        randomSource.setSeed(seed);
    }

    @Override
    public int nextIntInRange(int minInclusive, int maxInclusive) {
        int rangeSize = maxInclusive - minInclusive + 1;
        return minInclusive + randomSource.nextInt(rangeSize);
    }
}
