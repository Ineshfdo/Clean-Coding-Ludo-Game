package utils.randomgenerator;

// Seed-free (ISP): consumers only depend on drawing a number.
public interface RandomNumberGenerator {

    int nextIntInRange(int minInclusive, int maxInclusive);
}
