package numbergenerator;

import java.util.Random;

// Singleton: every component that needs random (dice, mystery cells, future strategies) must draw from the same seeded sequence otherwise a fixed seed would not make the whole game reproducible.
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
