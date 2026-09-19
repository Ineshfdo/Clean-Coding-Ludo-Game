package model.player;

import config.enums.PlayerColor;

// Display text derived from a PlayerColor - kept out of PlayerColor itself so the enum stays
// a pure list of constants.
public final class PlayerColorLabels {

    private PlayerColorLabels() {
    }

    public static String displayNameOf(PlayerColor color) {
        String lowerCaseName = color.name().toLowerCase();
        return Character.toUpperCase(lowerCaseName.charAt(0)) + lowerCaseName.substring(1);
    }

    public static String shortCodeOf(PlayerColor color) {
        return color.name().substring(0, 1);
    }
}
