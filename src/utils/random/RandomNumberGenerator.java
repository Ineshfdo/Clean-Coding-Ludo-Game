package utils.random;

// Kept seed-free (ISP) so consumers like Dice only depend on
// drawing a number.
public interface RandomNumberGenerator {

    int nextIntInRange(int minInclusive, int maxInclusive);
}
