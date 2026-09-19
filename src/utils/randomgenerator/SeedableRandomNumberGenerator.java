package utils.randomgenerator;

// Separate (ISP): only callers needing reproducible sequences can reseed.
public interface SeedableRandomNumberGenerator extends RandomNumberGenerator {

    void setSeed(long seed);
}
