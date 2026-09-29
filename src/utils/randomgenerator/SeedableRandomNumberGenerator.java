package utils.randomgenerator;

// A random number source that can be given a seed.
 
public interface SeedableRandomNumberGenerator extends RandomNumberGenerator {

    /**
    Restart the random sequence with a given starting value
    @param seed the seed; the same seed always gives the same sequence
    */
    void setSeed(long seed);
}
