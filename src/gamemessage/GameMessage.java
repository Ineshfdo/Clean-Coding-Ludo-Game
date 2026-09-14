package gamemessage;

import ludoboard.PlayerColor;

// Carries what happened; game logic never decides wording -
// that's ConsoleGameObserver's job.
public final class GameMessage {

    private final GameMessageType type;
    private final PlayerColor color;
    private final int rollValue;
    private final int roundNumber;
    private final int newPosition;
    private final String pieceLabel;
    private final String homeStraightCellLabel;

    private GameMessage(GameMessageType type, PlayerColor color, int rollValue,
            int roundNumber, int newPosition, String pieceLabel, String homeStraightCellLabel) {
        this.type = type;
        this.color = color;
        this.rollValue = rollValue;
        this.roundNumber = roundNumber;
        this.newPosition = newPosition;
        this.pieceLabel = pieceLabel;
        this.homeStraightCellLabel = homeStraightCellLabel;
    }

    public static GameMessage of(GameMessageType type) {
        return new GameMessage(type, null, 0, 0, 0, null, null);
    }

    public static GameMessage diceRolled(PlayerColor color, int rollValue) {
        return new GameMessage(GameMessageType.DICE_ROLLED, color, rollValue, 0, 0, null, null);
    }

    public static GameMessage tossWon(PlayerColor color, int rollValue) {
        return new GameMessage(GameMessageType.TOSS_WON, color, rollValue, 0, 0, null, null);
    }

    public static GameMessage tossTied(int rollValue) {
        return new GameMessage(GameMessageType.TOSS_TIED, null, rollValue, 0, 0, null, null);
    }

    public static GameMessage roundStarted(int roundNumber) {
        return new GameMessage(GameMessageType.ROUND_STARTED, null, 0, roundNumber, 0, null, null);
    }

    public static GameMessage turnStarted(PlayerColor color) {
        return new GameMessage(GameMessageType.TURN_STARTED, color, 0, 0, 0, null, null);
    }

    public static GameMessage turnRolled(PlayerColor color, int rollValue) {
        return new GameMessage(GameMessageType.TURN_ROLLED, color, rollValue, 0, 0, null, null);
    }

    public static GameMessage noPieceMovable() {
        return new GameMessage(GameMessageType.NO_PIECE_MOVABLE, null, 0, 0, 0, null, null);
    }

    public static GameMessage pieceMoved(String pieceLabel, int newPosition) {
        return new GameMessage(
                GameMessageType.PIECE_MOVED, null, 0, 0, newPosition, pieceLabel, null);
    }

    public static GameMessage pieceEnteredBoard(String pieceLabel, int newPosition) {
        return new GameMessage(
                GameMessageType.PIECE_ENTERED_BOARD, null, 0, 0, newPosition, pieceLabel, null);
    }

    public static GameMessage pieceEnteredHomeStraight(String pieceLabel, String cellLabel) {
        return new GameMessage(
                GameMessageType.PIECE_ENTERED_HOME_STRAIGHT, null, 0, 0, 0, pieceLabel, cellLabel);
    }

    public static GameMessage pieceReachedHome(String pieceLabel) {
        return new GameMessage(
                GameMessageType.PIECE_REACHED_HOME, null, 0, 0, 0, pieceLabel, null);
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

    public int getRoundNumber() {
        return roundNumber;
    }

    public int getNewPosition() {
        return newPosition;
    }

    public String getPieceLabel() {
        return pieceLabel;
    }

    public String getHomeStraightCellLabel() {
        return homeStraightCellLabel;
    }
}
