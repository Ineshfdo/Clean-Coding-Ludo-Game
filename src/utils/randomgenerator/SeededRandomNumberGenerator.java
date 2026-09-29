package utils.randomgenerator;

import java.util.Random;

// The only source of randomness in the game (Singleton).

public final class SeededRandomNumberGenerator implements SeedableRandomNumberGenerator {

    // The single instance, created once and shared by the whole game.
    private static final SeededRandomNumberGenerator SHARED_INSTANCE =
        new SeededRandomNumberGenerator();

    // Java's built-in random generator.
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

        // nextInt returns 0-based. add minInclusive to shift into range.
        return minInclusive + randomSource.nextInt(rangeSize);
    }
}
