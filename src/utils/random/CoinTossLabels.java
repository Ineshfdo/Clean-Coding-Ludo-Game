package utils.random;

import config.enums.CoinTossResult;

// T-1: display text for a coin toss result - kept out of CoinTossResult itself so the enum
// stays a pure list of constants.
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
