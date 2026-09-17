package gamemessage;

import java.util.List;

import ludoboard.PlayerColor;

// Carries what happened; game logic never decides wording -
// that's ConsoleGameObserver's job.
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

    private GameMessage(GameMessageType type, PlayerColor color, int rollValue,
            int roundNumber, int fromPosition, int newPosition, String pieceLabel,
            String homeStraightCellLabel, String capturedPieceLabel, String coinTossResultLabel,
            String movementDirectionLabel, String blockTypeLabel, String destinationLabel,
            String effectLabel, List<PlayerColor> finalStandings) {
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
    }

    public static GameMessage of(GameMessageType type) {
        return new GameMessage(type, null, 0, 0, 0, 0, null, null, null, null, null, null, null, null, null);
    }

    public static GameMessage diceRolled(PlayerColor color, int rollValue) {
        return new GameMessage(
                GameMessageType.DICE_ROLLED, color, rollValue, 0, 0, 0, null, null, null, null, null,
                null, null, null, null);
    }

    public static GameMessage tossWon(PlayerColor color, int rollValue) {
        return new GameMessage(
                GameMessageType.TOSS_WON, color, rollValue, 0, 0, 0, null, null, null, null, null,
                null, null, null, null);
    }

    public static GameMessage tossTied(int rollValue) {
        return new GameMessage(
                GameMessageType.TOSS_TIED, null, rollValue, 0, 0, 0, null, null, null, null, null,
                null, null, null, null);
    }

    public static GameMessage roundStarted(int roundNumber) {
        return new GameMessage(
                GameMessageType.ROUND_STARTED, null, 0, roundNumber, 0, 0, null, null, null, null,
                null, null, null, null, null);
    }

    public static GameMessage turnStarted(PlayerColor color) {
        return new GameMessage(
                GameMessageType.TURN_STARTED, color, 0, 0, 0, 0, null, null, null, null, null,
                null, null, null, null);
    }

    public static GameMessage turnRolled(PlayerColor color, int rollValue) {
        return new GameMessage(
                GameMessageType.TURN_ROLLED, color, rollValue, 0, 0, 0, null, null, null, null, null,
                null, null, null, null);
    }

    public static GameMessage noPieceMovable() {
        return new GameMessage(
                GameMessageType.NO_PIECE_MOVABLE, null, 0, 0, 0, 0, null, null, null, null, null,
                null, null, null, null);
    }

    // T-13: shows both endpoints of the move, not just the destination.
    public static GameMessage pieceMoved(String pieceLabel, int fromPosition, int newPosition) {
        return new GameMessage(
                GameMessageType.PIECE_MOVED, null, 0, 0, fromPosition, newPosition, pieceLabel, null,
                null, null, null, null, null, null, null);
    }

    // T-4/T-13: a block move also reports its chosen direction, classification, and both endpoints.
    public static GameMessage blockMoved(
            String pieceLabel, int fromPosition, int newPosition, String blockTypeLabel,
            String movementDirectionLabel) {
        return new GameMessage(
                GameMessageType.PIECE_MOVED, null, 0, 0, fromPosition, newPosition, pieceLabel, null,
                null, null, movementDirectionLabel, blockTypeLabel, null, null, null);
    }

    public static GameMessage pieceEnteredBoard(String pieceLabel, int newPosition) {
        return new GameMessage(
                GameMessageType.PIECE_ENTERED_BOARD, null, 0, 0, 0, newPosition, pieceLabel, null,
                null, null, null, null, null, null, null);
    }

    public static GameMessage pieceDirectionAssigned(
            String pieceLabel, String coinTossResultLabel, String movementDirectionLabel) {
        return new GameMessage(
                GameMessageType.PIECE_DIRECTION_ASSIGNED, null, 0, 0, 0, 0, pieceLabel, null, null,
                coinTossResultLabel, movementDirectionLabel, null, null, null, null);
    }

    public static GameMessage pieceEnteredHomeStraight(String pieceLabel, String cellLabel) {
        return new GameMessage(
                GameMessageType.PIECE_ENTERED_HOME_STRAIGHT, null, 0, 0, 0, 0, pieceLabel, cellLabel,
                null, null, null, null, null, null, null);
    }

    public static GameMessage pieceReachedHome(String pieceLabel) {
        return new GameMessage(
                GameMessageType.PIECE_REACHED_HOME, null, 0, 0, 0, 0, pieceLabel, null, null,
                null, null, null, null, null, null);
    }

    public static GameMessage pieceCaptured(String capturingPieceLabel, String capturedPieceLabel) {
        return new GameMessage(
                GameMessageType.PIECE_CAPTURED, null, 0, 0, 0, 0, capturingPieceLabel, null,
                capturedPieceLabel, null, null, null, null, null, null);
    }

    public static GameMessage blockCaptured(String capturingBlockLabel, String capturedBlockLabel) {
        return new GameMessage(
                GameMessageType.BLOCK_CAPTURED, null, 0, 0, 0, 0, capturingBlockLabel, null,
                capturedBlockLabel, null, null, null, null, null, null);
    }

    public static GameMessage boardStateReported(int roundNumber) {
        return new GameMessage(
                GameMessageType.BOARD_STATE_REPORTED, null, 0, roundNumber, 0, 0, null, null,
                null, null, null, null, null, null, null);
    }

    public static GameMessage pieceBlocked(String pieceLabel) {
        return new GameMessage(
                GameMessageType.PIECE_BLOCKED, null, 0, 0, 0, 0, pieceLabel, null, null, null,
                null, null, null, null, null);
    }

    public static GameMessage pieceNeedsExactRoll(String pieceLabel) {
        return new GameMessage(
                GameMessageType.PIECE_NEEDS_EXACT_ROLL, null, 0, 0, 0, 0, pieceLabel, null, null,
                null, null, null, null, null, null);
    }

    public static GameMessage blockRollTooSmall(String pieceLabel) {
        return new GameMessage(
                GameMessageType.BLOCK_ROLL_TOO_SMALL, null, 0, 0, 0, 0, pieceLabel, null, null,
                null, null, null, null, null, null);
    }

    public static GameMessage pieceLeftBlock(String pieceLabel) {
        return new GameMessage(
                GameMessageType.PIECE_LEFT_BLOCK, null, 0, 0, 0, 0, pieceLabel, null, null, null,
                null, null, null, null, null);
    }

    // T-10: the mystery cell's first spawn, at a random empty standard-path cell.
    public static GameMessage mysteryCellAppeared(int cellPosition) {
        return new GameMessage(
                GameMessageType.MYSTERY_CELL_APPEARED, null, 0, 0, 0, cellPosition, null, null,
                null, null, null, null, null, null, null);
    }

    // T-10: the mystery cell relocating after its four rounds at the previous cell.
    public static GameMessage mysteryCellRelocated(int cellPosition) {
        return new GameMessage(
                GameMessageType.MYSTERY_CELL_RELOCATED, null, 0, 0, 0, cellPosition, null, null,
                null, null, null, null, null, null, null);
    }

    // T-11: landing on the Mystery Cell teleports to a randomly chosen destination.
    // newPosition is -1 when destinationLabel is Base - there is no track cell.
    public static GameMessage pieceTeleported(String pieceLabel, String destinationLabel, int newPosition) {
        return new GameMessage(
                GameMessageType.PIECE_TELEPORTED, null, 0, 0, 0, newPosition, pieceLabel, null,
                null, null, null, null, destinationLabel, null, null);
    }

    // T-12: a coin toss assigns this piece its own Energized/Sick status.
    public static GameMessage individualEffectAssigned(String pieceLabel, String effectLabel) {
        return new GameMessage(
                GameMessageType.INDIVIDUAL_EFFECT_ASSIGNED, null, 0, 0, 0, 0, pieceLabel, null,
                null, null, null, null, null, effectLabel, null);
    }

    // T-12: a coin toss assigns the whole teleported block a shared Energized/Sick status.
    public static GameMessage blockEffectAssigned(String blockLabel, String effectLabel) {
        return new GameMessage(
                GameMessageType.BLOCK_EFFECT_ASSIGNED, null, 0, 0, 0, 0, blockLabel, null,
                null, null, null, null, null, effectLabel, null);
    }

    // T-12: a Sick effect halved this roll down to zero cells.
    public static GameMessage effectRollTooSmall(String pieceLabel) {
        return new GameMessage(
                GameMessageType.EFFECT_ROLL_TOO_SMALL, null, 0, 0, 0, 0, pieceLabel, null, null,
                null, null, null, null, null, null);
    }

    // T-13: two consecutive rounds of rolling a 3 forced this Beta-restricted piece/block to Base.
    public static GameMessage betaRestrictionTriggered(String pieceLabel) {
        return new GameMessage(
                GameMessageType.BETA_RESTRICTION_TRIGGERED, null, 0, 0, 0, 0, pieceLabel, null, null,
                null, null, null, null, null, null);
    }

    // T-13: announces that a just-teleported piece/block cannot move for the next 4 rounds.
    public static GameMessage betaRestrictionApplied(String pieceLabel) {
        return new GameMessage(
                GameMessageType.BETA_RESTRICTION_APPLIED, null, 0, 0, 0, 0, pieceLabel, null, null,
                null, null, null, null, null, null);
    }

    // T-14: a Gamma teleport reversed this piece/block's direction (Clockwise <-> Counter-Clockwise).
    public static GameMessage pieceDirectionReversed(String pieceLabel, String newDirectionLabel) {
        return new GameMessage(
                GameMessageType.PIECE_DIRECTION_REVERSED, null, 0, 0, 0, 0, pieceLabel, null, null,
                null, newDirectionLabel, null, null, null, null);
    }

    // GAME_OVER: the game ends once 3 of the 4 players have every piece Home - finalStandings
    // is ranked 1st..4th, with the one remaining unfinished player placed last.
    public static GameMessage gameOver(List<PlayerColor> finalStandings) {
        return new GameMessage(
                GameMessageType.GAME_OVER, null, 0, 0, 0, 0, null, null, null, null, null, null,
                null, null, finalStandings);
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
}
