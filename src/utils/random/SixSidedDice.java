package utils.random;

import config.constant.DiceConstants;

// Singleton: one physical die is shared by every player's turn.
public final class SixSidedDice implements Dice {

    private static final SixSidedDice SHARED_INSTANCE =
        new SixSidedDice(SeededRandomNumberGenerator.getInstance());

    private final RandomNumberGenerator numberGenerator;

    private SixSidedDice(RandomNumberGenerator numberGenerator) {
        this.numberGenerator = numberGenerator;
    }

    public static SixSidedDice getInstance() {
        return SHARED_INSTANCE;
    }

    @Override
    public int roll() {
        return numberGenerator.nextIntInRange(DiceConstants.LOWEST_FACE_VALUE, DiceConstants.HIGHEST_FACE_VALUE);
    }
}
