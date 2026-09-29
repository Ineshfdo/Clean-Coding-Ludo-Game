package utils.coin;

import config.enums.CoinTossResult;
import utils.randomgenerator.RandomNumberGenerator;
import utils.randomgenerator.SeededRandomNumberGenerator;

// The coin of the game (Singleton).
// It draws from the same seeded sequence as the dice.
 
public final class SeededCoinToss implements CoinToss {

    private static final int TAILS_VALUE = 0;
    private static final int HEADS_VALUE = 1;

    // The single coin instance, created once and shared by the whole game.
    private static final SeededCoinToss SHARED_INSTANCE =
        new SeededCoinToss(SeededRandomNumberGenerator.getInstance());

    // Shared RNG this coin draws from.
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
