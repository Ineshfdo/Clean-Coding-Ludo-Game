package model.direction;

import config.enums.CoinTossResult;

/**
 The direction given to a piece that leaves Base, and the coin toss that decided it (T-1).
 */
public final class EntryDirection {

    private final CoinTossResult tossResult;
    private final MovementDirectionStrategy direction;

    /**
     Creates the result of an entry toss.
     @param tossResult the result of the coin toss
     @param direction the direction that the result stands for
     */
    public EntryDirection(CoinTossResult tossResult, MovementDirectionStrategy direction) {
        this.tossResult = tossResult;
        this.direction = direction;
    }

    /**
     Gives the coin toss.
     @return the result of the coin toss
     */
    public CoinTossResult getTossResult() {
        return tossResult;
    }

    /**
     Gives the direction.
     @return the direction of the piece
     */
    public MovementDirectionStrategy getDirection() {
        return direction;
    }
}
