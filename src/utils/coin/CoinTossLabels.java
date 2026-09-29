package utils.coin;

import config.enums.CoinTossResult;


public final class CoinTossLabels {

    private CoinTossLabels() {
    }

    /**
    Gives the text shown on the console.
    @param result the result of the coin toss
    @return Heads or Tails
    */
    public static String labelOf(CoinTossResult result) {
        return switch (result) {
            case HEADS -> "Heads";
            case TAILS -> "Tails";
        };
    }
}
