package model.direction;

import config.enums.CoinTossResult;
import utils.coin.CoinToss;

// T-1: heads means clockwise, tails means counter-clockwise.
public final class CoinTossEntryDirectionAssigner implements EntryDirectionAssigner {

    private final CoinToss coinToss;

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
