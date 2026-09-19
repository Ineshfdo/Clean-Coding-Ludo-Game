package model.effect.mysterycell;

import config.enums.MysteryCellDestinationType;

// T-11: display text for each destination, kept out of the enum.
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
