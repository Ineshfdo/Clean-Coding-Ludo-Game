package utils.coin;

import config.enums.CoinTossResult;

/**
 * Decides heads or tails, for example for the direction of a piece (T-1).
 */
public interface CoinToss {

    /**
     * Flips the coin.
     *
     * @return heads or tails
     */
    CoinTossResult flip();
}
