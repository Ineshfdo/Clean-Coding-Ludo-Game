package gameoutput;

import gamemessage.GameMessage;
import gamemessage.GameMessageObserver;
import java.util.List;
import ludoboard.PlayerColor;
import player.Piece;
import player.Player;

// Observer: turns a GameMessage into the actual command-line text.
// Holds the player roster only to render the board-state report -
// game logic never tells this class what to say.
public final class ConsoleGameObserver implements GameMessageObserver {

    private static final PlayerColor[] BOARD_STATE_DISPLAY_ORDER =
            { PlayerColor.GREEN, PlayerColor.YELLOW, PlayerColor.BLUE, PlayerColor.RED };

    private final List<Player> players;

    public ConsoleGameObserver(List<Player> players) {
        this.players = players;
    }

    @Override
    public void onGameMessage(GameMessage message) {
        System.out.println(describe(message));
    }

    private String describe(GameMessage message) {
        return switch (message.getType()) {
            case GAME_INITIALIZING -> "Initializing Game Facade...";
            case BOARD_INITIALIZED -> "Board initialized.";
            case DICE_INITIALIZED -> "Dice initialized.";
            case PLAYERS_CREATED -> "Players created.";
            case GAME_STARTING -> "Starting the Ludo game!\n";
            case TOSS_STARTING -> "Rolling dice to determine who goes first...";
            case DICE_ROLLED ->
                    message.getColor() + " Player rolled a " + message.getRollValue();
            case TOSS_TIED ->
                    "There was a tie for the highest roll (" + message.getRollValue()
                            + ")! Everyone rerolls...\n";
            case TOSS_WON ->
                    message.getColor() + " Player won the toss with a "
                            + message.getRollValue() + " and goes first!";
            case ROUND_STARTED ->
                    "\n" + message.getRoundNumber() + ". Round " + message.getRoundNumber();
            case TURN_STARTED -> "- " + message.getColor() + " Player's Turn -";
            case TURN_ROLLED -> "  -> Rolled a " + message.getRollValue();
            case NO_PIECE_MOVABLE -> "  -> No pieces on the board could be moved.";
            case PIECE_MOVED ->
                    "  -> Moved " + message.getPieceLabel() + " to cell "
                            + message.getNewPosition() + ".";
            case PIECE_ENTERED_BOARD ->
                    "  -> " + message.getPieceLabel()
                            + " left Base and entered the board at (X) position cell "
                            + message.getNewPosition() + ".";
            case PIECE_ENTERED_HOME_STRAIGHT ->
                    "  -> " + message.getPieceLabel() + " entered its HomeStraight at "
                            + message.getHomeStraightCellLabel() + ".";
            case PIECE_REACHED_HOME ->
                    "  -> " + message.getPieceLabel() + " reached Home and is removed from play!";
            case PIECE_CAPTURED ->
                    "  -> " + message.getPieceLabel() + " captured " + message.getCapturedPieceLabel()
                            + "! " + message.getCapturedPieceLabel() + " returns to Base.";
            case THIRD_SIX_VOIDED ->
                    "  -> Three sixes in a row! This roll is void - turn passes to the next player.";
            case BOARD_STATE_REPORTED -> describeBoardState(message.getRoundNumber());
        };
    }

    private String describeBoardState(int roundNumber) {
        StringBuilder report = new StringBuilder();
        report.append("\nRound ").append(roundNumber).append(" Current Board State\n");
        report.append("==================\n");
        report.append("-------------------------------");

        for (PlayerColor color : BOARD_STATE_DISPLAY_ORDER) {
            report.append('\n').append(describePlayerRow(findPlayer(color)));
        }

        report.append("\n-------------------------------");
        return report.toString();
    }

    private static String describePlayerRow(Player player) {
        StringBuilder row = new StringBuilder();
        row.append(player.getColor()).append(':');

        List<Piece> pieces = player.getPieces();
        for (int index = 0; index < pieces.size(); index++) {
            row.append(index == 0 ? " " : "  ");
            row.append(describePiece(pieces.get(index), player.getCaptureCount()));
        }

        return row.toString();
    }

    private static String describePiece(Piece piece, int captureCount) {
        return piece + "(" + describeLocation(piece) + ", Caps:" + captureCount + ")";
    }

    private static String describeLocation(Piece piece) {
        if (piece.isAtBase()) {
            return "BASE";
        }
        if (piece.isOnHomeStraight()) {
            return "HomeStraight(" + piece.getHomeStraightIndex() + ")";
        }
        if (piece.isHome()) {
            return "HOME";
        }
        return "Cell(" + piece.getTrackPosition() + ")";
    }

    private Player findPlayer(PlayerColor color) {
        return players.stream()
                .filter(player -> player.getColor() == color)
                .findFirst()
                .orElseThrow();
    }
}
