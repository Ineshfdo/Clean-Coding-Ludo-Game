package gameoutput;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import direction.MovementDirectionStrategy;
import gamemessage.GameMessage;
import gamemessage.GameMessageObserver;
import ludoboard.Board;
import ludoboard.PlayerColor;
import player.BlockDirectionStrategy;
import player.BlockDirectionType;
import player.Piece;
import player.Player;

// Observer: turns a GameMessage into console text. Holds roster/board only to render reports.
public final class ConsoleGameObserver implements GameMessageObserver {

    private static final int BLOCKADE_PIECE_COUNT = 2;
    private static final PlayerColor[] BOARD_STATE_DISPLAY_ORDER =
            { PlayerColor.GREEN, PlayerColor.YELLOW, PlayerColor.BLUE, PlayerColor.RED };

    private final List<Player> players;
    private final Board board;
    private final BlockDirectionStrategy blockDirectionStrategy;

    public ConsoleGameObserver(
            List<Player> players, Board board, BlockDirectionStrategy blockDirectionStrategy) {
        this.players = players;
        this.board = board;
        this.blockDirectionStrategy = blockDirectionStrategy;
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
            case PIECE_MOVED -> describePieceMoved(message);
            case PIECE_ENTERED_BOARD ->
                    "  -> " + message.getPieceLabel()
                            + " left Base and entered the board at (X) position cell "
                            + message.getNewPosition() + ".";
            case PIECE_DIRECTION_ASSIGNED ->
                    "  -> Coin toss for " + message.getPieceLabel() + ": "
                            + message.getCoinTossResultLabel() + " - it will move "
                            + message.getMovementDirectionLabel() + ".";
            case PIECE_ENTERED_HOME_STRAIGHT ->
                    "  -> " + message.getPieceLabel() + " entered its HomeStraight at "
                            + message.getHomeStraightCellLabel() + ".";
            case PIECE_REACHED_HOME ->
                    "  -> " + message.getPieceLabel() + " reached Home and is removed from play!";
            case PIECE_CAPTURED ->
                    "  -> " + message.getPieceLabel() + " captured " + message.getCapturedPieceLabel()
                            + "! " + message.getCapturedPieceLabel() + " returns to Base.";
            case PIECE_BLOCKED ->
                    "  -> " + message.getPieceLabel()
                            + " is blocked by an opponent's blockade and cannot move.";
            case PIECE_NEEDS_EXACT_ROLL ->
                    "  -> " + message.getPieceLabel()
                            + " needs an exact roll to reach Home and cannot move.";
            case BLOCK_ROLL_TOO_SMALL ->
                    "  -> " + message.getPieceLabel()
                            + "'s block roll divided to zero cells and cannot move.";
            case THIRD_SIX_VOIDED ->
                    "  -> Three sixes in a row! This roll is void - turn passes to the next player.";
            case BOARD_STATE_REPORTED -> describeBoardState(message.getRoundNumber());
        };
    }

    // T-4: a block move also names its BlockType and BlockDirection; a solo move does not.
    private static String describePieceMoved(GameMessage message) {
        String outcome = "  -> Moved " + message.getPieceLabel() + " to cell "
                + message.getNewPosition() + ".";
        if (message.getBlockTypeLabel() == null) {
            return outcome;
        }
        return outcome + " [BlockType:" + message.getBlockTypeLabel()
                + " BlockDirection:" + message.getMovementDirectionLabel() + "]";
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

    private String describePlayerRow(Player player) {
        StringBuilder row = new StringBuilder();
        row.append(player.getColor()).append(':');

        List<String> segments = buildPieceSegments(player);
        for (int index = 0; index < segments.size(); index++) {
            row.append(index == 0 ? " " : "  ");
            row.append(segments.get(index));
        }

        return row.toString();
    }

    // T-3: same-color pieces sharing a track cell form a block, shown as one segment.
    private List<String> buildPieceSegments(Player player) {
        List<Piece> pieces = player.getPieces();
        List<String> segments = new ArrayList<>();
        Set<Piece> alreadyShown = new HashSet<>();

        for (Piece piece : pieces) {
            if (alreadyShown.contains(piece)) {
                continue;
            }

            List<Piece> blockGroup = findBlockGroup(piece, pieces);
            if (blockGroup.size() >= BLOCKADE_PIECE_COUNT) {
                segments.add(describeBlock(blockGroup, player.getCaptureCount()));
                alreadyShown.addAll(blockGroup);
            } else {
                segments.add(describePiece(piece, player.getCaptureCount()));
                alreadyShown.add(piece);
            }
        }

        return segments;
    }

    // Rule 10: same-index HomeStraight pieces also form a block, moving together.
    private static List<Piece> findBlockGroup(Piece piece, List<Piece> allPieces) {
        List<Piece> group = new ArrayList<>();

        for (Piece candidate : allPieces) {
            if (sharesLocation(candidate, piece)) {
                group.add(candidate);
            }
        }
        return group;
    }

    private static boolean sharesLocation(Piece candidate, Piece piece) {
        if (piece.isOnTrack()) {
            return candidate.isOnTrack() && candidate.getTrackPosition() == piece.getTrackPosition();
        }
        if (piece.isOnHomeStraight()) {
            return candidate.isOnHomeStraight()
                    && candidate.getHomeStraightIndex() == piece.getHomeStraightIndex();
        }
        return false;
    }

    // T-1: only a track block has a direction to choose - HomeStraight has no branching.
    private String describeBlock(List<Piece> blockedPieces, int captureCount) {
        StringBuilder block = new StringBuilder("[Block:");
        for (Piece piece : blockedPieces) {
            block.append(' ').append(describePiece(piece, captureCount));
        }
        if (blockedPieces.get(0).isOnTrack()) {
            Piece dominantPiece = blockDirectionStrategy.resolveDominantPiece(blockedPieces, board);
            block.append(" BlockType:").append(BlockDirectionType.classify(blockedPieces).getLabel());
            block.append(" BlockDirection:").append(dominantPiece.getMovementDirectionStrategy().getLabel());
            block.append(" BlockApproachCellPasses:").append(dominantPiece.getApproachPassCount());
        }
        block.append(']');
        return block.toString();
    }

    private String describePiece(Piece piece, int captureCount) {
        return piece + "(" + describeLocation(piece) + describeDirection(piece)
                + ", Caps:" + captureCount + ")";
    }

    // T-1: shown only after a coin toss assigns direction - never for Base or Home.
    private String describeDirection(Piece piece) {
        if (piece.isAtBase() || piece.isHome()) {
            return "";
        }
        MovementDirectionStrategy direction = piece.getMovementDirectionStrategy();
        return ", " + direction.getLabel() + ", ApproachCellPasses:" + piece.getApproachPassCount();
    }

    private String describeLocation(Piece piece) {
        if (piece.isAtBase()) {
            return "BASE";
        }
        if (piece.isOnHomeStraight()) {
            return "HomeStraight(" + piece.getHomeStraightIndex() + ")";
        }
        if (piece.isHome()) {
            return "HOME";
        }

        int position = piece.getTrackPosition();
        if (isApproachCell(position)) {
            return "Approach(" + position + ")";
        }
        return "Cell(" + position + ")";
    }

    private boolean isApproachCell(int position) {
        for (PlayerColor color : PlayerColor.values()) {
            if (board.getApproachCellPosition(color) == position) {
                return true;
            }
        }
        return false;
    }

    private Player findPlayer(PlayerColor color) {
        return players.stream()
                .filter(player -> player.getColor() == color)
                .findFirst()
                .orElseThrow();
    }
}
