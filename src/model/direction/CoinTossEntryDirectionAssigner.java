package model.direction;

import config.enums.CoinTossResult;
import utils.coin.CoinToss;

/**
 Gives a piece its direction by a coin toss: heads means clockwise and tails means counter-clockwise (T-1).
 */
public final class CoinTossEntryDirectionAssigner implements EntryDirectionAssigner {

    private final CoinToss coinToss;

    /**
     Creates the assigner.
     @param coinToss the coin that is tossed for every piece that leaves Base
     */
    public CoinTossEntryDirectionAssigner(CoinToss coinToss) {
        this.coinToss = coinToss;
    }

    @Override
    public EntryDirection assign() {
        CoinTossResult tossResult = coinToss.flip();
        MovementDirectionStrategy direction = tossResult == CoinTossResult.HEADS
                ? ClockwiseMovementStrategy.getInstance()
                : CounterClockwiseMovementStrategy.getInstance();

        return new EntryDirection(tossResult, direction);
    }
}
