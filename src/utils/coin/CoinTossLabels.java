package utils.coin;

import config.enums.CoinTossResult;

// T-1: display text for a coin toss result, kept out of the enum.
public final class CoinTossLabels {

    private CoinTossLabels() {
    }

    public static String labelOf(CoinTossResult result) {
        return switch (result) {
            case HEADS -> "Heads";
            case TAILS -> "Tails";
        };
    }
}
