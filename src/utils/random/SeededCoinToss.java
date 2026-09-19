package utils.random;
import config.enums.CoinTossResult;


// Singleton: shares the dice's seeded sequence, so direction stays reproducible too.
public final class SeededCoinToss implements CoinToss {

    private static final int TAILS_VALUE = 0;
    private static final int HEADS_VALUE = 1;

    private static final SeededCoinToss SHARED_INSTANCE =
            new SeededCoinToss(SeededRandomNumberGenerator.getInstance());

    private final RandomNumberGenerator numberGenerator;

    private SeededCoinToss(RandomNumberGenerator numberGenerator) {
        this.numberGenerator = numberGenerator;
    }

    public static SeededCoinToss getInstance() {
        return SHARED_INSTANCE;
    }

    @Override
    public CoinTossResult flip() {
        int drawnValue = numberGenerator.nextIntInRange(TAILS_VALUE, HEADS_VALUE);
        return drawnValue == HEADS_VALUE ? CoinTossResult.HEADS : CoinTossResult.TAILS;
    }
}
