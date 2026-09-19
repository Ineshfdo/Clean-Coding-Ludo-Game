package model.player;

import config.enums.PlayerColor;

// Display text for a PlayerColor, kept out of the enum.
public final class PlayerColorNames {

    private PlayerColorNames() {
    }

    public static String displayNameOf(PlayerColor color) {
        String lowerCaseName = color.name().toLowerCase();

        return Character.toUpperCase(lowerCaseName.charAt(0)) + lowerCaseName.substring(1);
    }

    public static String shortCodeOf(PlayerColor color) {
        return color.name().substring(0, 1);
    }
}
