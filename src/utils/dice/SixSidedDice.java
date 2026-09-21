package utils.dice;

import config.constant.DiceConstants;
import utils.randomgenerator.RandomNumberGenerator;
import utils.randomgenerator.SeededRandomNumberGenerator;

// Singleton: one physical die is shared by every player's turn.
public final class SixSidedDice implements Dice {

    private static final SixSidedDice SHARED_INSTANCE =
        new SixSidedDice(SeededRandomNumberGenerator.getInstance());

    private final RandomNumberGenerator randomNumberGenerator;

    private SixSidedDice(RandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
    }

    public static SixSidedDice getInstance() {
        return SHARED_INSTANCE;
    }

    @Override
    public int roll() {
        return randomNumberGenerator.nextIntInRange(DiceConstants.LOWEST_FACE_VALUE, DiceConstants.HIGHEST_FACE_VALUE);
    }
}
