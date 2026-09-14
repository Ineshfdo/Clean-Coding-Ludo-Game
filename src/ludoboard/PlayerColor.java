package ludoboard;

public enum PlayerColor {

    RED, YELLOW, GREEN, BLUE;

    public String getDisplayName() {
        String lowerCaseName = name().toLowerCase();
        return Character.toUpperCase(lowerCaseName.charAt(0)) + lowerCaseName.substring(1);
    }

    public String getShortCode() {
        return name().substring(0, 1);
    }
}
