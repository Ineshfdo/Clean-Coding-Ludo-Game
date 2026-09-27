package utils.randomgenerator;

/**
 * A random number source that can be given a seed. Only the callers that need a repeatable
 * sequence, such as the start-up code, use it (ISP).
 */
public interface SeedableRandomNumberGenerator extends RandomNumberGenerator {

    /**
     * Restarts the sequence.
     *
     * @param seed the seed; the same seed always gives the same sequence
     */
    void setSeed(long seed);
}
