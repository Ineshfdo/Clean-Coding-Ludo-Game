package gameoutput;

import direction.MovementDirectionStrategy;
import gamemessage.GameMessage;
import gamemessage.GameMessageObserver;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ludoboard.Board;
import ludoboard.PlayerColor;
import player.BlockDirectionStrategy;
import player.BlockDirectionType;
import player.MovementEffect;
import player.Piece;
import player.Player;

// Observer: turns a GameMessage into console text. Holds roster/board only to render reports.
public final class ConsoleGameObserver implements GameMessageObserver {

    private static final int BLOCKADE_PIECE_COUNT = 2;
    private static final PlayerColor[] BOARD_STATE_DISPLAY_ORDER =
            { PlayerColor.GREEN, PlayerColor.YELLOW, PlayerColor.BLUE, PlayerColor.RED };
    private static final String MYSTERY_CELL_BANNER_BORDER = "=".repeat(40);

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
                    "A Mystery Cell has appeared at cell " + message.getNewPosition() + "!");
            case MYSTERY_CELL_RELOCATED -> describeMysteryCellBanner(
                    "The Mystery Cell has relocated to cell " + message.getNewPosition() + ".");
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
        };
    }

    // T-11: a Base destination has no track cell to report - every other destination does.
    private static String describePieceTeleported(GameMessage message) {
        String outcome = "  -> " + message.getPieceLabel() + " landed on the Mystery Cell! Teleported to "
                + message.getDestinationLabel();
        if (message.getNewPosition() < 0) {
            return outcome + ".";
        }
        return outcome + " (cell " + message.getNewPosition() + ").";
    }

    // T-4/T-13: a block move also names its BlockType and BlockDirection; a solo move does not.
    private static String describePieceMoved(GameMessage message) {
        String outcome = "  -> Moved " + message.getPieceLabel() + " from cell "
                + message.getFromPosition() + " to cell " + message.getNewPosition() + ".";
        if (message.getBlockTypeLabel() == null) {
            return outcome;
        }
        return outcome + " [BlockType:" + message.getBlockTypeLabel()
                + " BlockDirection:" + message.getMovementDirectionLabel() + "]";
    }

    // T-10: the mystery cell spawn/relocate wording is bordered so it stands out on the console.
    private static String describeMysteryCellBanner(String messageText) {
        return "\n" + MYSTERY_CELL_BANNER_BORDER + "\n" + messageText + "\n" + MYSTERY_CELL_BANNER_BORDER;
    }

    private String describeBoardState(int roundNumber) {
        StringBuilder report = new StringBuilder();
        report.append("\nRound ").append(roundNumber).append(" Current Board State\n");
        report.append("==================\n");
        report.append("-------------------------------\n");

        for (PlayerColor color : BOARD_STATE_DISPLAY_ORDER) {
            report.append(describePlayerRow(findPlayer(color))).append("\n\n");
        }

        report.append("-------------------------------");
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
                segments.add(describeBlock(blockGroup));
                alreadyShown.addAll(blockGroup);
            } else {
                segments.add(describePiece(piece));
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
    private String describeBlock(List<Piece> blockedPieces) {
        StringBuilder block = new StringBuilder("[Block:");
        for (Piece piece : blockedPieces) {
            block.append(' ').append(describePiece(piece));
        }
        if (blockedPieces.get(0).isOnTrack()) {
            MovementDirectionStrategy travelDirection =
                    blockDirectionStrategy.resolveTravelDirection(blockedPieces, board);
            Piece naturalMember = findNaturalMemberFor(blockedPieces, travelDirection);
            block.append(" BlockType:").append(BlockDirectionType.classify(blockedPieces).getLabel());
            block.append(" BlockDirection:").append(travelDirection.getLabel());
            block.append(" BlockApproachCellPasses:").append(naturalMember.getApproachPassCount());
        }
        // T-12: every member shares this identical value, so it is shown once for the whole block.
        MovementEffect blockEffect = blockedPieces.get(0).getBlockEffect();
        if (blockEffect.isActive()) {
            block.append(" BlockEffect:").append(blockEffect.getLabel());
        }
        block.append(']');
        return block.toString();
    }

    // T-4/T-5: the member whose OWN direction matches the block's chosen travel direction -
    // its approach-pass count is what BlockApproachCellPasses reports.
    private static Piece findNaturalMemberFor(List<Piece> blockedPieces, MovementDirectionStrategy travelDirection) {
        return blockedPieces.stream()
                .filter(piece -> piece.getOriginalMovementDirectionStrategy() == travelDirection)
                .findFirst()
                .orElse(blockedPieces.get(0));
    }

    // T-9: each piece shows its OWN capture count - HomeStraightEligibilityRule gates on this, not the team total.
    private String describePiece(Piece piece) {
        return piece + "(" + describeLocation(piece) + describeDirection(piece)
                + ", IndividualCaptureCount:" + piece.getCaptureCount() + describeIndividualEffect(piece)
                + describeRestriction(piece) + ")";
    }

    // T-12: only shown while this piece's own Energized/Sick status is active - most pieces never have one.
    private static String describeIndividualEffect(Piece piece) {
        if (!piece.getIndividualEffect().isActive()) {
            return "";
        }
        return ", IndividualEffect:" + piece.getIndividualEffect().getLabel();
    }

    // T-13: only shown while this piece is still Beta-restricted - most pieces never have one.
    private static String describeRestriction(Piece piece) {
        if (!piece.getRestrictionState().forbidsMovement()) {
            return "";
        }
        return ", BetaRestrictionRoundsLeft:" + piece.getRestrictionState().getRoundsRemaining();
    }

    // T-1/T-5: shown only after a coin toss assigns direction - never for Base or Home.
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
