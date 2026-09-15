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
    private final String capturedPieceLabel;
    private final String coinTossResultLabel;
    private final String movementDirectionLabel;
    private final String blockTypeLabel;

    private GameMessage(GameMessageType type, PlayerColor color, int rollValue,
            int roundNumber, int newPosition, String pieceLabel, String homeStraightCellLabel,
            String capturedPieceLabel, String coinTossResultLabel, String movementDirectionLabel,
            String blockTypeLabel) {
        this.type = type;
        this.color = color;
        this.rollValue = rollValue;
        this.roundNumber = roundNumber;
        this.newPosition = newPosition;
        this.pieceLabel = pieceLabel;
        this.homeStraightCellLabel = homeStraightCellLabel;
        this.capturedPieceLabel = capturedPieceLabel;
        this.coinTossResultLabel = coinTossResultLabel;
        this.movementDirectionLabel = movementDirectionLabel;
        this.blockTypeLabel = blockTypeLabel;
    }

    public static GameMessage of(GameMessageType type) {
        return new GameMessage(type, null, 0, 0, 0, null, null, null, null, null, null);
    }

    public static GameMessage diceRolled(PlayerColor color, int rollValue) {
        return new GameMessage(
                GameMessageType.DICE_ROLLED, color, rollValue, 0, 0, null, null, null, null, null,
                null);
    }

    public static GameMessage tossWon(PlayerColor color, int rollValue) {
        return new GameMessage(
                GameMessageType.TOSS_WON, color, rollValue, 0, 0, null, null, null, null, null,
                null);
    }

    public static GameMessage tossTied(int rollValue) {
        return new GameMessage(
                GameMessageType.TOSS_TIED, null, rollValue, 0, 0, null, null, null, null, null,
                null);
    }

    public static GameMessage roundStarted(int roundNumber) {
        return new GameMessage(
                GameMessageType.ROUND_STARTED, null, 0, roundNumber, 0, null, null, null, null,
                null, null);
    }

    public static GameMessage turnStarted(PlayerColor color) {
        return new GameMessage(
                GameMessageType.TURN_STARTED, color, 0, 0, 0, null, null, null, null, null, null);
    }

    public static GameMessage turnRolled(PlayerColor color, int rollValue) {
        return new GameMessage(
                GameMessageType.TURN_ROLLED, color, rollValue, 0, 0, null, null, null, null, null,
                null);
    }

    public static GameMessage noPieceMovable() {
        return new GameMessage(
                GameMessageType.NO_PIECE_MOVABLE, null, 0, 0, 0, null, null, null, null, null,
                null);
    }

    public static GameMessage pieceMoved(String pieceLabel, int newPosition) {
        return new GameMessage(
                GameMessageType.PIECE_MOVED, null, 0, 0, newPosition, pieceLabel, null, null, null,
                null, null);
    }

    // T-4: a block move also reports its chosen direction and same/opposite classification.
    public static GameMessage blockMoved(
            String pieceLabel, int newPosition, String blockTypeLabel, String movementDirectionLabel) {
        return new GameMessage(
                GameMessageType.PIECE_MOVED, null, 0, 0, newPosition, pieceLabel, null, null, null,
                movementDirectionLabel, blockTypeLabel);
    }

    public static GameMessage pieceEnteredBoard(String pieceLabel, int newPosition) {
        return new GameMessage(
                GameMessageType.PIECE_ENTERED_BOARD, null, 0, 0, newPosition, pieceLabel, null,
                null, null, null, null);
    }

    public static GameMessage pieceDirectionAssigned(
            String pieceLabel, String coinTossResultLabel, String movementDirectionLabel) {
        return new GameMessage(
                GameMessageType.PIECE_DIRECTION_ASSIGNED, null, 0, 0, 0, pieceLabel, null, null,
                coinTossResultLabel, movementDirectionLabel, null);
    }

    public static GameMessage pieceEnteredHomeStraight(String pieceLabel, String cellLabel) {
        return new GameMessage(
                GameMessageType.PIECE_ENTERED_HOME_STRAIGHT, null, 0, 0, 0, pieceLabel, cellLabel,
                null, null, null, null);
    }

    public static GameMessage pieceReachedHome(String pieceLabel) {
        return new GameMessage(
                GameMessageType.PIECE_REACHED_HOME, null, 0, 0, 0, pieceLabel, null, null, null,
                null, null);
    }

    public static GameMessage pieceCaptured(String capturingPieceLabel, String capturedPieceLabel) {
        return new GameMessage(
                GameMessageType.PIECE_CAPTURED, null, 0, 0, 0, capturingPieceLabel, null,
                capturedPieceLabel, null, null, null);
    }

    public static GameMessage blockCaptured(String capturingBlockLabel, String capturedBlockLabel) {
        return new GameMessage(
                GameMessageType.BLOCK_CAPTURED, null, 0, 0, 0, capturingBlockLabel, null,
                capturedBlockLabel, null, null, null);
    }

    public static GameMessage boardStateReported(int roundNumber) {
        return new GameMessage(
                GameMessageType.BOARD_STATE_REPORTED, null, 0, roundNumber, 0, null, null, null,
                null, null, null);
    }

    public static GameMessage pieceBlocked(String pieceLabel) {
        return new GameMessage(
                GameMessageType.PIECE_BLOCKED, null, 0, 0, 0, pieceLabel, null, null, null, null,
                null);
    }

    public static GameMessage pieceNeedsExactRoll(String pieceLabel) {
        return new GameMessage(
                GameMessageType.PIECE_NEEDS_EXACT_ROLL, null, 0, 0, 0, pieceLabel, null, null, null,
                null, null);
    }

    public static GameMessage blockRollTooSmall(String pieceLabel) {
        return new GameMessage(
                GameMessageType.BLOCK_ROLL_TOO_SMALL, null, 0, 0, 0, pieceLabel, null, null, null,
                null, null);
    }

    public static GameMessage pieceLeftBlock(String pieceLabel) {
        return new GameMessage(
                GameMessageType.PIECE_LEFT_BLOCK, null, 0, 0, 0, pieceLabel, null, null, null, null,
                null);
    }

    // T-10: the mystery cell's first spawn, at a random empty standard-path cell.
    public static GameMessage mysteryCellAppeared(int cellPosition) {
        return new GameMessage(
                GameMessageType.MYSTERY_CELL_APPEARED, null, 0, 0, cellPosition, null, null, null,
                null, null, null);
    }

    // T-10: the mystery cell relocating after its four rounds at the previous cell.
    public static GameMessage mysteryCellRelocated(int cellPosition) {
        return new GameMessage(
                GameMessageType.MYSTERY_CELL_RELOCATED, null, 0, 0, cellPosition, null, null, null,
                null, null, null);
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

    public String getCapturedPieceLabel() {
        return capturedPieceLabel;
    }

    public String getCoinTossResultLabel() {
        return coinTossResultLabel;
    }

    public String getMovementDirectionLabel() {
        return movementDirectionLabel;
    }

    public String getBlockTypeLabel() {
        return blockTypeLabel;
    }
}
