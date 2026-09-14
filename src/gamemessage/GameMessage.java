package gamemessage;

import ludoboard.PlayerColor;

// Carries what happened. Game logic only ever builds these; it never decides wording - that is ConsoleGameObserver's job.
public final class GameMessage {

    private final GameMessageType type;
    private final PlayerColor color;
    private final int rollValue;

    private GameMessage(GameMessageType type, PlayerColor color, int rollValue) {
        this.type = type;
        this.color = color;
        this.rollValue = rollValue;
    }

    public static GameMessage of(GameMessageType type) {
        return new GameMessage(type, null, 0);
    }

    public static GameMessage diceRolled(PlayerColor color, int rollValue) {
        return new GameMessage(GameMessageType.DICE_ROLLED, color, rollValue);
    }

    public static GameMessage tossWon(PlayerColor color, int rollValue) {
        return new GameMessage(GameMessageType.TOSS_WON, color, rollValue);
    }

    public GameMessageType getType() {
        return type;
    }

    public PlayerColor getColor() {
        return color;
    }

    public int getRollValue() {
        return rollValue;
    }
}
