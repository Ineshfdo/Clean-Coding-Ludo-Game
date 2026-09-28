package utils.randomgenerator;

import java.util.Random;

/**
 The only source of randomness in the game (Singleton).
 All random numbers come from one seeded sequence, so the same seed always gives the same game.
 */
public final class SeededRandomNumberGenerator implements SeedableRandomNumberGenerator {

    private static final SeededRandomNumberGenerator SHARED_INSTANCE =
            new SeededRandomNumberGenerator();

    private final Random randomSource;

    private SeededRandomNumberGenerator() {
        this.randomSource = new Random();
    }

    /**
     Gives the one shared generator.
     @return the shared instance
     */
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
