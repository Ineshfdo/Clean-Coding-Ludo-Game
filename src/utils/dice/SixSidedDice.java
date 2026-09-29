package utils.dice;

import config.constant.DiceConstants;
import utils.randomgenerator.RandomNumberGenerator;
import utils.randomgenerator.SeededRandomNumberGenerator;

// The die of the game (Singleton).
// One die is shared by every player.
// Its numbers come from the seeded random generator.

public final class SixSidedDice implements Dice {

    // The single die instance, created once and shared by the whole game.
    private static final SixSidedDice SHARED_INSTANCE =
        new SixSidedDice(SeededRandomNumberGenerator.getInstance());

    // Shared RNG this die draws from.
    private final RandomNumberGenerator randomNumberGenerator;

    private SixSidedDice(RandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
    }

    /**
     Gives the one shared die.
     @return the shared instance
     */
    public static SixSidedDice getInstance() {
        return SHARED_INSTANCE;
    }

    @Override
    public int roll() {
        return randomNumberGenerator.nextIntInRange(DiceConstants.LOWEST_FACE_VALUE, DiceConstants.HIGHEST_FACE_VALUE);
    }
}
