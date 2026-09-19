package view;

import config.constant.BlockadeConstants;
import config.enums.PlayerColor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import message.GameMessage;
import message.observer.GameMessageObserver;
import model.board.Board;
import model.direction.MovementDirectionStrategy;
import model.piece.Piece;
import model.player.Player;
import model.player.PlayerColorNames;
import model.player.strategy.blockdirection.BlockDirectionClassifier;
import model.player.strategy.blockdirection.BlockTravelDirectionStrategy;

// Turns each GameMessage into console text; keeps roster and board only for reports.
public final class ConsoleGameObserver implements GameMessageObserver {

    private static final PlayerColor[] BOARD_STATE_DISPLAY_ORDER =
            { PlayerColor.GREEN, PlayerColor.YELLOW, PlayerColor.BLUE, PlayerColor.RED };

    private static final String MYSTERY_CELL_BANNER_BORDER = "=".repeat(40);
    private static final String GAME_OVER_BANNER_BORDER = "=".repeat(50);
    private static final String BOARD_STATE_BANNER_BORDER = "=".repeat(37);

    private final List<Player> players;
    private final Board board;
    private final BlockTravelDirectionStrategy blockTravelDirectionStrategy;

    public ConsoleGameObserver(
            List<Player> players, Board board, BlockTravelDirectionStrategy blockTravelDirectionStrategy) {
        this.players = players;
        this.board = board;
        this.blockTravelDirectionStrategy = blockTravelDirectionStrategy;
    }

    @Override
    public void onGameMessage(GameMessage message) {
        System.out.println(describe(message));
    }

    private String describe(GameMessage message) {
        return switch (message.getType()) {
            case PLAYER_ROSTER_ANNOUNCED ->
                    describePlayerRoster(message.getColor(), message.getPieceLabels());
            case GAME_STARTING -> "\nStarting the Ludo game!\n";
            case TOSS_STARTING -> "Rolling The Dice To Determine Who Goes First\n--------------------------------------------\n";
            case DICE_ROLLED ->
                    message.getColor() + " Player rolls a " + message.getRollValue();
            case TOSS_TIED ->
                    "There Was A Tie For The Highest Roll (" + message.getRollValue()
                            + ")! EVERYONE REROLLS...\n";
            case TOSS_WON ->
                    message.getColor() + " Player Won The Toss With A "
                            + message.getRollValue() + " And Goes First!";
            case ROUND_STARTED ->
                    "\n" + message.getRoundNumber() + ". Round " + message.getRoundNumber();
            case TURN_STARTED -> "\n- " + message.getColor() + " Player's Turn -";
            case TURN_ROLLED ->
                    PlayerColorNames.displayNameOf(message.getColor()) + " player rolled " + message.getRollValue();
            case HOME_GATE_OPENED ->
                    "The home gate opens for the " + PlayerColorNames.displayNameOf(message.getColor())
                            + " player: no opponent pieces remain to capture.";
            case NO_PIECE_MOVABLE -> "  -> No pieces on the board could be moved.";
            case PIECE_MOVED -> describePieceMoved(message);
            case PIECE_ENTERED_BOARD -> describePieceEnteredBoard(message);
            case PIECE_DIRECTION_ASSIGNED ->
                    "  -> Coin toss for " + message.getPieceLabel() + ": "
                            + message.getCoinTossResultLabel() + " - it will move "
                            + message.getMovementDirectionLabel() + ".";
            case PIECE_ENTERED_HOME_STRAIGHT ->
                    "  -> " + message.getPieceLabel() + " entered its HomeStraight at "
                            + message.getHomeStraightCellLabel() + ".";
            case PIECE_REACHED_HOME ->
                    "  -> " + message.getPieceLabel() + " reached Home and is removed from play!";
            case PIECE_CAPTURED -> describePieceCaptured(message);
            case BLOCK_CAPTURED ->
                    "  -> Blockade " + message.getPieceLabel() + " captured Blockade "
                            + message.getCapturedPieceLabel() + "! All of "
                            + message.getCapturedPieceLabel() + " returns to Base.";
            case PIECE_BLOCKED ->
                    "  -> " + message.getPieceLabel()
                            + " is blocked by an opponent's blockade and cannot move.";
            case PIECE_NEEDS_EXACT_ROLL ->
                    "  -> " + message.getPieceLabel()
                            + " needs an exact roll to reach Home and cannot move.";
            case BLOCK_ROLL_TOO_SMALL ->
                    "  -> " + message.getPieceLabel()
                            + "'s block roll divided to zero cells and cannot move.";
            case PIECE_LEFT_BLOCK ->
                    "  -> " + message.getPieceLabel()
                            + " leaves the block and resumes its own direction.";
            case THIRD_SIX_VOIDED ->
                    "  -> Three sixes in a row! This roll is void - turn passes to the next player.";
            case MYSTERY_CELL_APPEARED -> describeMysteryCellBanner(
                    "A Mystery Cell has appeared at cell " + message.getNewPosition()
                            + " will be here and will be at that location for the next 4 rounds.");
            case MYSTERY_CELL_RELOCATED -> describeMysteryCellBanner(
                    "The Mystery Cell has relocated to cell " + message.getNewPosition()
                            + " will be here and will be at that location for the next 4 rounds.");
            case PIECE_TELEPORTED -> describePieceTeleported(message);
            case INDIVIDUAL_EFFECT_ASSIGNED ->
                    "  -> " + message.getPieceLabel() + " is now " + message.getEffectLabel()
                            + " (individual effect, lasts 4 rounds).";
            case BLOCK_EFFECT_ASSIGNED ->
                    "  -> Block " + message.getPieceLabel() + " is now " + message.getEffectLabel()
                            + " (block effect, lasts 4 rounds, overrides individual effects).";
            case EFFECT_ROLL_TOO_SMALL ->
                    "  -> " + message.getPieceLabel()
                            + "'s Sick effect halved this roll to zero cells and cannot move.";
            case BETA_RESTRICTION_APPLIED ->
                    "  -> " + message.getPieceLabel()
                            + " cannot move for the next 4 rounds (Beta restriction).";
            case BETA_RESTRICTION_TRIGGERED ->
                    "  -> " + message.getPieceLabel()
                            + " rolled a 3 two rounds in a row while Beta-restricted and is sent back to Base!";
            case PIECE_DIRECTION_REVERSED ->
                    "  -> " + message.getPieceLabel() + " landed on Gamma and reversed direction - now moving "
                            + message.getMovementDirectionLabel() + ".";
            case BOARD_STATE_REPORTED -> describeBoardState(message.getRoundNumber());
            case GAME_OVER -> describeGameOver(message.getFinalStandings());
        };
    }

    // 3.1: "The red player has four (04) pieces named R1, R2, R3, and R4."
    private static String describePlayerRoster(PlayerColor color, List<String> pieceLabels) {
        return "The " + color.name().toLowerCase() + " player has "
                + describePieceCountInWords(pieceLabels.size()) + " ("
                + String.format("%02d", pieceLabels.size()) + ") pieces named "
                + joinWithAnd(pieceLabels) + ".";
    }

    private static String describePieceCountInWords(int pieceCount) {
        return switch (pieceCount) {
            case 4 -> "four";
            default -> String.valueOf(pieceCount);
        };
    }

    // "R1, R2, R3, and R4": comma-separated, with "and" before the last.
    private static String joinWithAnd(List<String> labels) {
        if (labels.size() == 1) {
            return labels.get(0);
        }

        String allButLast = String.join(", ", labels.subList(0, labels.size() - 1));

        return allButLast + ", and " + labels.get(labels.size() - 1);
    }

    // GAME_OVER: ranks 1st..4th in finishing order.
    private static String describeGameOver(List<PlayerColor> finalStandings) {
        String[] placeLabels = { "1st", "2nd", "3rd", "4th" };
        StringBuilder banner = new StringBuilder();
        banner.append('\n').append(GAME_OVER_BANNER_BORDER).append('\n');
        banner.append("                   GAME OVER!\n");
        banner.append(GAME_OVER_BANNER_BORDER).append('\n');
        banner.append("FINAL STANDINGS:\n");

        for (int rank = 0; rank < finalStandings.size(); rank++) {
            banner.append(placeLabels[rank]).append(" Place: ")
                    .append(finalStandings.get(rank).name()).append('\n');
        }

        banner.append(GAME_OVER_BANNER_BORDER);

        return banner.toString();
    }

    // T-11: a Base destination has no cell to report.
    private static String describePieceTeleported(GameMessage message) {
        String outcome = "  -> " + message.getPieceLabel() + " landed on the Mystery Cell! Teleported to "
                + message.getDestinationLabel();

        if (message.getNewPosition() < 0) {
            return outcome + ".";
        }

        return outcome + " (cell " + message.getNewPosition() + ").";
    }

    // T-4/T-13: block moves name BlockType/BlockDirection; solo moves name dice and direction.
    private String describePieceMoved(GameMessage message) {
        if (message.getBlockTypeLabel() != null) {
            return "  -> Moved " + message.getPieceLabel() + " from cell "
                    + message.getFromPosition() + " to cell " + message.getNewPosition() + "."
                    + " [BlockType:" + message.getBlockTypeLabel()
                    + " BlockDirection:" + message.getMovementDirectionLabel() + "]";
        }

        return "  -> " + PlayerColorNames.displayNameOf(message.getColor()) + " moves piece " + message.getPieceLabel()
                + " from location " + describeCellLabel(message.getFromPosition())
                + " to " + describeCellLabel(message.getNewPosition())
                + " by " + message.getRollValue() + " units in "
                + message.getMovementDirectionLabel() + " direction.";
    }

    // Names a cell "Approach(X)" for an Approach cell, else "Cell(X)".
    private String describeCellLabel(int position) {
        if (isApproachCell(position)) {
            return "Approach(" + position + ")";
        }

        return "Cell(" + position + ")";
    }

    // Requirement 4: the landing square, then the captured player's new tally.
    private static String describePieceCaptured(GameMessage message) {
        String captureLine = "  -> " + message.getPieceLabel() + " piece lands on square "
                + message.getNewPosition() + ", captures " + message.getCapturedPieceLabel()
                + " and returns it to the base.";
        String tallyLine = "  -> " + PlayerColorNames.displayNameOf(message.getColor()) + " player now has "
                + message.getPiecesOnBoard() + "/4 pieces on the board and " + message.getPiecesAtBase()
                + "/4 pieces on the base.";

        return captureLine + "\n" + tallyLine;
    }

    // Reports the piece leaving Base, then the player's tally (from the message counts).
    private static String describePieceEnteredBoard(GameMessage message) {
        String colorName = PlayerColorNames.displayNameOf(message.getColor());
        String movedLine = "  -> " + colorName + " player moves piece " + message.getPieceLabel()
                + " to the starting point.";
        String tallyLine = "  -> " + colorName + " player now has " + message.getPiecesOnBoard()
                + "/4 pieces on the board and " + message.getPiecesAtBase() + "/4 pieces on the base.";

        return movedLine + "\n" + tallyLine;
    }

    // T-10: Mystery Cell banners are bordered to stand out.
    private static String describeMysteryCellBanner(String messageText) {
        return "\n" + MYSTERY_CELL_BANNER_BORDER + "\n" + messageText + "\n" + MYSTERY_CELL_BANNER_BORDER;
    }

    // Requirement 5: a per-player board/base tally, printed before the board-state dump.
    private String describeRoundStatusSummary() {
        StringBuilder summary = new StringBuilder("\n-------------------------------\n");

        for (PlayerColor color : BOARD_STATE_DISPLAY_ORDER) {
            Player player = findPlayer(color);
            summary.append(PlayerColorNames.displayNameOf(color)).append(" player now has ")
                    .append(player.countPiecesOnBoard()).append("/4 pieces on the board and ")
                    .append(player.countPiecesAtBase()).append("/4 pieces on the base.\n");
        }

        summary.append("-------------------------------\n");

        return summary.toString();
    }

    private String describeBoardState(int roundNumber) {
        StringBuilder report = new StringBuilder();
        report.append(describeRoundStatusSummary());
        report.append("\nRound ").append(roundNumber).append(" Current Board State\n");
        report.append(BOARD_STATE_BANNER_BORDER).append('\n');

        List<String> playerRows = new ArrayList<>();
        for (PlayerColor color : BOARD_STATE_DISPLAY_ORDER) {
            playerRows.add(describePlayerRow(findPlayer(color)));
        }
        report.append(String.join("\n\n", playerRows));

        report.append('\n').append(BOARD_STATE_BANNER_BORDER);

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

    // T-3: same-color pieces on one cell form a block, shown as one segment.
    private List<String> buildPieceSegments(Player player) {
        List<Piece> pieces = player.getPieces();
        List<String> segments = new ArrayList<>();
        Set<Piece> alreadyShown = new HashSet<>();

        for (Piece piece : pieces) {
            if (alreadyShown.contains(piece)) {
                continue;
            }

            List<Piece> blockGroup = findBlockGroup(piece, pieces);

            if (blockGroup.size() >= BlockadeConstants.MINIMUM_BLOCKADE_SIZE) {
                segments.add(describeBlock(blockGroup));
                alreadyShown.addAll(blockGroup);
            } else {
                segments.add(describePiece(piece));
                alreadyShown.add(piece);
            }
        }

        return segments;
    }

    // Rule 10: same-index HomeStraight pieces also form a block.
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

    // T-1: only a track block has a direction; HomeStraight has none to choose.
    private String describeBlock(List<Piece> blockedPieces) {
        StringBuilder block = new StringBuilder("[Block:");

        for (Piece piece : blockedPieces) {
            block.append(' ').append(describePiece(piece));
        }

        if (blockedPieces.get(0).isOnTrack()) {
            MovementDirectionStrategy travelDirection =
                    blockTravelDirectionStrategy.resolveTravelDirection(blockedPieces, board);
            Piece naturalMember = findNaturalMemberFor(blockedPieces, travelDirection);
            block.append(" BlockType:")
                    .append(BlockDirectionClassifier.labelOf(BlockDirectionClassifier.classify(blockedPieces)));
            block.append(" BlockDirection:").append(travelDirection.getLabel());
            block.append(" BlockApproachCellPasses:").append(naturalMember.getApproachPassCount());
        }

        // T-12: shown once for the block, only if assigned to this exact group.
        Piece blockRepresentative = blockedPieces.get(0);

        if (blockRepresentative.hasActiveBlockEffectForSize(blockedPieces.size())) {
            block.append(" BlockEffect:").append(blockRepresentative.getBlockEffect().getLabel());
        }

        block.append(']');

        return block.toString();
    }

    // T-4/T-5: the member whose own direction matches the block's; its pass count is reported.
    private static Piece findNaturalMemberFor(List<Piece> blockedPieces, MovementDirectionStrategy travelDirection) {
        return blockedPieces.stream()
                .filter(piece -> piece.getOriginalMovementDirectionStrategy() == travelDirection)
                .findFirst()
                .orElse(blockedPieces.get(0));
    }

    // T-9: each piece shows its own capture count (HomeStraight entry depends on it).
    private String describePiece(Piece piece) {
        return piece + "(" + describeLocation(piece) + describeDirection(piece)
                + ", IndividualCaptureCount:" + piece.getCaptureCount() + describeIndividualEffect(piece)
                + describeRestriction(piece) + ")";
    }

    // T-12: shown only while the piece's own Energized/Sick effect is active.
    private static String describeIndividualEffect(Piece piece) {
        if (!piece.getIndividualEffect().isActive()) {
            return "";
        }

        return ", IndividualEffect:" + piece.getIndividualEffect().getLabel();
    }

    // T-13: shown only while the piece is Beta-restricted.
    private static String describeRestriction(Piece piece) {
        if (!piece.getRestrictionState().forbidsMovement()) {
            return "";
        }

        return ", BetaRestrictionRoundsLeft:" + piece.getRestrictionState().getRoundsRemaining();
    }

    // T-1/T-5: shown only after a coin toss sets direction, never for Base or Home.
    private String describeDirection(Piece piece) {
        if (piece.isAtBase() || piece.isHome()) {
            return "";
        }

        MovementDirectionStrategy direction = piece.getMovementDirectionStrategy();
        MovementDirectionStrategy originalDirection = piece.getOriginalMovementDirectionStrategy();

        return ", CurrentDirection:" + direction.getLabel() + ", OriginalDirection:" + originalDirection.getLabel()
                + ", ApproachCellPasses:" + piece.getApproachPassCount();
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
