package model.effect;

import config.enums.MysteryCellDestinationType;

// T-11: display text for each Mystery Cell destination - kept out of
// MysteryCellDestinationType itself so the enum stays a pure list of constants.
public final class MysteryCellDestinationLabels {

    private MysteryCellDestinationLabels() {
    }

    public static String labelOf(MysteryCellDestinationType destinationType) {
        return switch (destinationType) {
            case ALPHA -> "Alpha";
            case BETA -> "Beta";
            case GAMMA -> "Gamma";
            case BASE -> "Base";
            case ENTRY -> "Entry";
            case APPROACH -> "Approach";
        };
    }
}
