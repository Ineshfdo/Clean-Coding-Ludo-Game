package utils.coin;

import config.enums.CoinTossResult;
import utils.randomgenerator.RandomNumberGenerator;
import utils.randomgenerator.SeededRandomNumberGenerator;

/**
 The coin of the game (Singleton).
 It draws from the same seeded sequence as the dice, so the results can be repeated.
 */
public final class SeededCoinToss implements CoinToss {

    private static final int TAILS_VALUE = 0;
    private static final int HEADS_VALUE = 1;

    private static final SeededCoinToss SHARED_INSTANCE =
            new SeededCoinToss(SeededRandomNumberGenerator.getInstance());

    private final RandomNumberGenerator randomNumberGenerator;

    private SeededCoinToss(RandomNumberGenerator randomNumberGenerator) {
        this.randomNumberGenerator = randomNumberGenerator;
    }

    /**
     Gives the one shared coin.
     @return the shared instance
     */
    public static SeededCoinToss getInstance() {
        return SHARED_INSTANCE;
    }

    @Override
    public CoinTossResult flip() {
        int drawnValue = randomNumberGenerator.nextIntInRange(TAILS_VALUE, HEADS_VALUE);

        return drawnValue == HEADS_VALUE ? CoinTossResult.HEADS : CoinTossResult.TAILS;
    }
}
