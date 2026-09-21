package utils.coin;

import config.enums.CoinTossResult;
import utils.randomgenerator.RandomNumberGenerator;
import utils.randomgenerator.SeededRandomNumberGenerator;

// Singleton: shares the dice's seeded sequence, so results are reproducible.
public final class SeededCoinToss implements CoinToss {

    private static final int TAILS_VALUE = 0;
    private static final int HEADS_VALUE = 1;

    private static final SeededCoinToss SHARED_INSTANCE =
            new SeededCoinToss(SeededRandomNumberGenerator.getInstance());

    private final RandomNumberGenerator randomNumberGenerator;

    private SeededCoinToss(RandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
    }

    public static SeededCoinToss getInstance() {
        return SHARED_INSTANCE;
    }

    @Override
    public CoinTossResult flip() {
        int drawnValue = randomNumberGenerator.nextIntInRange(TAILS_VALUE, HEADS_VALUE);

        return drawnValue == HEADS_VALUE ? CoinTossResult.HEADS : CoinTossResult.TAILS;
    }
}
