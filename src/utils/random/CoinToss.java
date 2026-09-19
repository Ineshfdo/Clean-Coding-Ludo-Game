package utils.random;
import config.enums.CoinTossResult;

// T-1: decides heads or tails for a piece entering the board.
public interface CoinToss {

    CoinTossResult flip();
}
