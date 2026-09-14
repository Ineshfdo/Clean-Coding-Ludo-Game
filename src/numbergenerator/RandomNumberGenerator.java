package numbergenerator;

// Kept seed-free (ISP) so consumers like Dice depend only on drawing a number, not on the ability to reseed it.
public interface RandomNumberGenerator {

    int nextIntInRange(int minInclusive, int maxInclusive);
}
