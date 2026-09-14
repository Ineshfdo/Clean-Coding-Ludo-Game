package numbergenerator;

// Separate from RandomNumberGenerator (ISP) so only callers
// needing reproducible sequences get reseed power.
public interface SeedableRandomNumberGenerator extends RandomNumberGenerator {

    void setSeed(long seed);
}
