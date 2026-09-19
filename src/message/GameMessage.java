package message;

import config.enums.GameMessageType;
import config.enums.PlayerColor;
import java.util.List;

// Carries what happened; ConsoleGameObserver decides the wording.
public final class GameMessage {

    private final GameMessageType type;
    private final PlayerColor color;

    private final int rollValue;
    private final int roundNumber;
    private final int fromPosition;
    private final int newPosition;

    private final String pieceLabel;
    private final String homeStraightCellLabel;
    private final String capturedPieceLabel;
    private final String coinTossResultLabel;
    private final String movementDirectionLabel;
    private final String blockTypeLabel;
    private final String destinationLabel;
    private final String effectLabel;

    private final List<PlayerColor> finalStandings;
    private final List<String> pieceLabels;

    private final int piecesOnBoard;
    private final int piecesAtBase;

    private GameMessage(GameMessageType type, PlayerColor color, int rollValue,
            int roundNumber, int fromPosition, int newPosition, String pieceLabel,
            String homeStraightCellLabel, String capturedPieceLabel, String coinTossResultLabel,
            String movementDirectionLabel, String blockTypeLabel, String destinationLabel,
            String effectLabel, List<PlayerColor> finalStandings, List<String> pieceLabels,
            int piecesOnBoard, int piecesAtBase) {
        this.type = type;
        this.color = color;

        this.rollValue = rollValue;
        this.roundNumber = roundNumber;
        this.fromPosition = fromPosition;
        this.newPosition = newPosition;

        this.pieceLabel = pieceLabel;
        this.homeStraightCellLabel = homeStraightCellLabel;
        this.capturedPieceLabel = capturedPieceLabel;
        this.coinTossResultLabel = coinTossResultLabel;
        this.movementDirectionLabel = movementDirectionLabel;
        this.blockTypeLabel = blockTypeLabel;
        this.destinationLabel = destinationLabel;
        this.effectLabel = effectLabel;

        this.finalStandings = finalStandings;
        this.pieceLabels = pieceLabels;

        this.piecesOnBoard = piecesOnBoard;
        this.piecesAtBase = piecesAtBase;
    }

    public static GameMessage of(GameMessageType type) {
        return new GameMessage(
                type, null, 0, 0, 0, 0, null, null, null, null, null, null, null, null, null, null, 0, 0);
    }

    public static GameMessage diceRolled(PlayerColor color, int rollValue) {
        return new GameMessage(
                GameMessageType.DICE_ROLLED, color, rollValue, 0, 0, 0, null, null, null, null, null,
                null, null, null, null, null, 0, 0);
    }

    public static GameMessage tossWon(PlayerColor color, int rollValue) {
        return new GameMessage(
                GameMessageType.TOSS_WON, color, rollValue, 0, 0, 0, null, null, null, null, null,
                null, null, null, null, null, 0, 0);
    }

    public static GameMessage tossTied(int rollValue) {
        return new GameMessage(
                GameMessageType.TOSS_TIED, null, rollValue, 0, 0, 0, null, null, null, null, null,
                null, null, null, null, null, 0, 0);
    }

    public static GameMessage roundStarted(int roundNumber) {
        return new GameMessage(
                GameMessageType.ROUND_STARTED, null, 0, roundNumber, 0, 0, null, null, null, null,
                null, null, null, null, null, null, 0, 0);
    }

    public static GameMessage turnStarted(PlayerColor color) {
        return new GameMessage(
                GameMessageType.TURN_STARTED, color, 0, 0, 0, 0, null, null, null, null, null,
                null, null, null, null, null, 0, 0);
    }

    public static GameMessage turnRolled(PlayerColor color, int rollValue) {
        return new GameMessage(
                GameMessageType.TURN_ROLLED, color, rollValue, 0, 0, 0, null, null, null, null, null,
                null, null, null, null, null, 0, 0);
    }

    public static GameMessage noPieceMovable() {
        return new GameMessage(
                GameMessageType.NO_PIECE_MOVABLE, null, 0, 0, 0, 0, null, null, null, null, null,
                null, null, null, null, null, 0, 0);
    }

    // Requirement 2: a solo move reports its dice value, direction and both endpoints.
    public static GameMessage pieceMoved(
            PlayerColor color, String pieceLabel, int fromPosition, int newPosition, int diceValue,
            String movementDirectionLabel) {
        return new GameMessage(
                GameMessageType.PIECE_MOVED, color, diceValue, 0, fromPosition, newPosition, pieceLabel,
                null, null, null, movementDirectionLabel, null, null, null, null, null, 0, 0);
    }

    // T-4/T-13: a block move reports its direction, type and both endpoints.
    public static GameMessage blockMoved(
            String pieceLabel, int fromPosition, int newPosition, String blockTypeLabel,
            String movementDirectionLabel) {
        return new GameMessage(
                GameMessageType.PIECE_MOVED, null, 0, 0, fromPosition, newPosition, pieceLabel, null,
                null, null, movementDirectionLabel, blockTypeLabel, null, null, null, null, 0, 0);
    }

    // Observer: reports the piece that left Base and the player's board/base tally.
    public static GameMessage pieceEnteredBoard(
            PlayerColor color, String pieceLabel, int newPosition, int piecesOnBoard, int piecesAtBase) {
        return new GameMessage(
                GameMessageType.PIECE_ENTERED_BOARD, color, 0, 0, 0, newPosition, pieceLabel, null,
                null, null, null, null, null, null, null, null, piecesOnBoard, piecesAtBase);
    }

    public static GameMessage pieceDirectionAssigned(
            String pieceLabel, String coinTossResultLabel, String movementDirectionLabel) {
        return new GameMessage(
                GameMessageType.PIECE_DIRECTION_ASSIGNED, null, 0, 0, 0, 0, pieceLabel, null, null,
                coinTossResultLabel, movementDirectionLabel, null, null, null, null, null, 0, 0);
    }

    public static GameMessage pieceEnteredHomeStraight(String pieceLabel, String cellLabel) {
        return new GameMessage(
                GameMessageType.PIECE_ENTERED_HOME_STRAIGHT, null, 0, 0, 0, 0, pieceLabel, cellLabel,
                null, null, null, null, null, null, null, null, 0, 0);
    }

    public static GameMessage pieceReachedHome(String pieceLabel) {
        return new GameMessage(
                GameMessageType.PIECE_REACHED_HOME, null, 0, 0, 0, 0, pieceLabel, null, null,
                null, null, null, null, null, null, null, 0, 0);
    }

    // Requirement 4: reports the capture cell, the captured piece and its player's new tally.
    public static GameMessage pieceCaptured(
            String capturingPieceLabel, int capturePosition, String capturedPieceLabel,
            PlayerColor capturedPlayerColor, int piecesOnBoard, int piecesAtBase) {
        return new GameMessage(
                GameMessageType.PIECE_CAPTURED, capturedPlayerColor, 0, 0, 0, capturePosition,
                capturingPieceLabel, null, capturedPieceLabel, null, null, null, null, null, null, null,
                piecesOnBoard, piecesAtBase);
    }

    public static GameMessage blockCaptured(String capturingBlockLabel, String capturedBlockLabel) {
        return new GameMessage(
                GameMessageType.BLOCK_CAPTURED, null, 0, 0, 0, 0, capturingBlockLabel, null,
                capturedBlockLabel, null, null, null, null, null, null, null, 0, 0);
    }

    public static GameMessage boardStateReported(int roundNumber) {
        return new GameMessage(
                GameMessageType.BOARD_STATE_REPORTED, null, 0, roundNumber, 0, 0, null, null,
                null, null, null, null, null, null, null, null, 0, 0);
    }

    public static GameMessage pieceBlocked(String pieceLabel) {
        return new GameMessage(
                GameMessageType.PIECE_BLOCKED, null, 0, 0, 0, 0, pieceLabel, null, null, null,
                null, null, null, null, null, null, 0, 0);
    }

    public static GameMessage pieceNeedsExactRoll(String pieceLabel) {
        return new GameMessage(
                GameMessageType.PIECE_NEEDS_EXACT_ROLL, null, 0, 0, 0, 0, pieceLabel, null, null,
                null, null, null, null, null, null, null, 0, 0);
    }

    public static GameMessage blockRollTooSmall(String pieceLabel) {
        return new GameMessage(
                GameMessageType.BLOCK_ROLL_TOO_SMALL, null, 0, 0, 0, 0, pieceLabel, null, null,
                null, null, null, null, null, null, null, 0, 0);
    }

    public static GameMessage pieceLeftBlock(String pieceLabel) {
        return new GameMessage(
                GameMessageType.PIECE_LEFT_BLOCK, null, 0, 0, 0, 0, pieceLabel, null, null, null,
                null, null, null, null, null, null, 0, 0);
    }

    // T-10: the Mystery Cell's first spawn, on a random empty cell.
    public static GameMessage mysteryCellAppeared(int cellPosition) {
        return new GameMessage(
                GameMessageType.MYSTERY_CELL_APPEARED, null, 0, 0, 0, cellPosition, null, null,
                null, null, null, null, null, null, null, null, 0, 0);
    }

    // T-10: the Mystery Cell relocates after four rounds.
    public static GameMessage mysteryCellRelocated(int cellPosition) {
        return new GameMessage(
                GameMessageType.MYSTERY_CELL_RELOCATED, null, 0, 0, 0, cellPosition, null, null,
                null, null, null, null, null, null, null, null, 0, 0);
    }

    // T-11: teleport to a random destination; newPosition is -1 for Base (no track cell).
    public static GameMessage pieceTeleported(String pieceLabel, String destinationLabel, int newPosition) {
        return new GameMessage(
                GameMessageType.PIECE_TELEPORTED, null, 0, 0, 0, newPosition, pieceLabel, null,
                null, null, null, null, destinationLabel, null, null, null, 0, 0);
    }

    // T-12: a coin toss gives this piece its own Energized/Sick status.
    public static GameMessage individualEffectAssigned(String pieceLabel, String effectLabel) {
        return new GameMessage(
                GameMessageType.INDIVIDUAL_EFFECT_ASSIGNED, null, 0, 0, 0, 0, pieceLabel, null,
                null, null, null, null, null, effectLabel, null, null, 0, 0);
    }

    // T-12: a coin toss gives the teleported block a shared Energized/Sick status.
    public static GameMessage blockEffectAssigned(String blockLabel, String effectLabel) {
        return new GameMessage(
                GameMessageType.BLOCK_EFFECT_ASSIGNED, null, 0, 0, 0, 0, blockLabel, null,
                null, null, null, null, null, effectLabel, null, null, 0, 0);
    }

    // T-12: a Sick effect cut the roll to zero cells.
    public static GameMessage effectRollTooSmall(String pieceLabel) {
        return new GameMessage(
                GameMessageType.EFFECT_ROLL_TOO_SMALL, null, 0, 0, 0, 0, pieceLabel, null, null,
                null, null, null, null, null, null, null, 0, 0);
    }

    // T-13: consecutive 3s sent a Beta-restricted piece/block back to Base.
    public static GameMessage betaRestrictionTriggered(String pieceLabel) {
        return new GameMessage(
                GameMessageType.BETA_RESTRICTION_TRIGGERED, null, 0, 0, 0, 0, pieceLabel, null, null,
                null, null, null, null, null, null, null, 0, 0);
    }

    // T-13: a teleported piece/block cannot move for 4 rounds.
    public static GameMessage betaRestrictionApplied(String pieceLabel) {
        return new GameMessage(
                GameMessageType.BETA_RESTRICTION_APPLIED, null, 0, 0, 0, 0, pieceLabel, null, null,
                null, null, null, null, null, null, null, 0, 0);
    }

    // T-14: a Gamma teleport reversed the piece/block's direction.
    public static GameMessage pieceDirectionReversed(String pieceLabel, String newDirectionLabel) {
        return new GameMessage(
                GameMessageType.PIECE_DIRECTION_REVERSED, null, 0, 0, 0, 0, pieceLabel, null, null,
                null, newDirectionLabel, null, null, null, null, null, 0, 0);
    }

    // Home gate: no opponent pieces remain, so the T-7 capture requirement is waived.
    public static GameMessage homeGateOpened(PlayerColor color) {
        return new GameMessage(
                GameMessageType.HOME_GATE_OPENED, color, 0, 0, 0, 0, null, null, null, null, null,
                null, null, null, null, null, 0, 0);
    }

    // 3.1: announces a player's pieces before the game begins.
    public static GameMessage playerRosterAnnounced(PlayerColor color, List<String> pieceLabels) {
        return new GameMessage(
                GameMessageType.PLAYER_ROSTER_ANNOUNCED, color, 0, 0, 0, 0, null, null, null, null,
                null, null, null, null, null, pieceLabels, 0, 0);
    }

    // GAME_OVER: standings ranked 1st..4th in finishing order.
    public static GameMessage gameOver(List<PlayerColor> finalStandings) {
        return new GameMessage(
                GameMessageType.GAME_OVER, null, 0, 0, 0, 0, null, null, null, null, null, null,
                null, null, finalStandings, null, 0, 0);
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

    public int getFromPosition() {
        return fromPosition;
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

    public String getDestinationLabel() {
        return destinationLabel;
    }

    public String getEffectLabel() {
        return effectLabel;
    }

    public List<PlayerColor> getFinalStandings() {
        return finalStandings;
    }

    public List<String> getPieceLabels() {
        return pieceLabels;
    }

    public int getPiecesOnBoard() {
        return piecesOnBoard;
    }

    public int getPiecesAtBase() {
        return piecesAtBase;
    }
}
