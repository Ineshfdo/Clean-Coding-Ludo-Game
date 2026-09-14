package numbergenerator;

// Separate from RandomNumberGenerator (ISP) so only callers that actually need reproducible sequences
public interface SeedableRandomNumberGenerator extends RandomNumberGenerator {

    void setSeed(long seed);
}
