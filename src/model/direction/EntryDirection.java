package model.direction;

import config.enums.CoinTossResult;

// T-1: the direction a piece gets when it leaves Base, and the coin toss that decided it.
public final class EntryDirection {

    private final CoinTossResult tossResult;
    private final MovementDirectionStrategy direction;

    public EntryDirection(CoinTossResult tossResult, MovementDirectionStrategy direction) {
        this.tossResult = tossResult;
        this.direction = direction;
    }

    public CoinTossResult getTossResult() {
        return tossResult;
    }

    public MovementDirectionStrategy getDirection() {
        return direction;
    }
}
