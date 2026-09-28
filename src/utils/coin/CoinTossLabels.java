package utils.coin;

import config.enums.CoinTossResult;

/**
 Gives the display text of a coin toss result, so the enum stays free of text.
 */
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
