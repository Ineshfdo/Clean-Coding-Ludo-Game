package dice;

import numbergenerator.RandomNumberGenerator;
import numbergenerator.SeededRandomNumberGenerator;

// Singleton one physical die is shared by every player's turn.
public final class SixSidedDice implements Dice {

    private static final int LOWEST_FACE_VALUE = 1;
    private static final int HIGHEST_FACE_VALUE = 6;

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
        return numberGenerator.nextIntInRange(LOWEST_FACE_VALUE, HIGHEST_FACE_VALUE);
    }
}
