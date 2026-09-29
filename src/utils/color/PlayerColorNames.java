package utils.color;

import config.enums.PlayerColor;

// Display text for a colour: full name (Red) and short code (R).
 
public final class PlayerColorNames {

    private PlayerColorNames() {
    }

    /**
    Gives the full name.
    @param color the colour
    @return the name with a capital first letter, for example Red
    */
    public static String displayNameOf(PlayerColor color) {
        String lowerCaseName = color.name().toLowerCase();

        return Character.toUpperCase(lowerCaseName.charAt(0)) + lowerCaseName.substring(1);
    }

    /**
     Gives the short code that starts the name of a piece.
     @param color the colour
     @return the first letter of the colour, for example R
     */
    public static String shortCodeOf(PlayerColor color) {
        return color.name().substring(0, 1);
    }
}
