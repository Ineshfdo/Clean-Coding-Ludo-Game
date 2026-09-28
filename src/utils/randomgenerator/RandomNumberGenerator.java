package utils.randomgenerator;

/**
 A source of random numbers.
 Users only need to draw a number, so this interface has no seed (ISP).
 */
public interface RandomNumberGenerator {

    /**
     Draws a random number.
     @param minInclusive the smallest number that may be drawn
     @param maxInclusive the largest number that may be drawn
     @return a number from minInclusive to maxInclusive, both included
     */
    int nextIntInRange(int minInclusive, int maxInclusive);
}
