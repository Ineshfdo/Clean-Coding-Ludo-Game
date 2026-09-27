package utils.dice;

import config.constant.DiceConstants;
import utils.randomgenerator.RandomNumberGenerator;
import utils.randomgenerator.SeededRandomNumberGenerator;

/**
 * The die of the game (Singleton). One die is shared by every player, in the same way as one
 * physical die is shared on a real board. Its numbers come from the seeded random generator, so a
 * game can be repeated.
 */
public final class SixSidedDice implements Dice {

    private static final SixSidedDice SHARED_INSTANCE =
        new SixSidedDice(SeededRandomNumberGenerator.getInstance());

    private final RandomNumberGenerator randomNumberGenerator;

    private SixSidedDice(RandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
    }

    /**
     * Gives the one shared die.
     *
     * @return the shared instance
     */
    public static SixSidedDice getInstance() {
        return SHARED_INSTANCE;
    }

    @Override
    public int roll() {
        return randomNumberGenerator.nextIntInRange(DiceConstants.LOWEST_FACE_VALUE, DiceConstants.HIGHEST_FACE_VALUE);
    }
}
