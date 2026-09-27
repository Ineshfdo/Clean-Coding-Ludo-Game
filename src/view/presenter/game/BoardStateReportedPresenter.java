package view.presenter.game;

import config.constant.BlockadeConstants;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import message.game.BoardStateReported;
import model.board.Board;
import model.direction.MovementDirectionStrategy;
import model.piece.Piece;
import model.player.Player;
import model.player.strategy.blockdirection.BlockDirectionClassifier;
import model.player.strategy.blockdirection.BlockTravelDirectionStrategy;
import utils.color.PlayerColorNames;
import view.presenter.CellNames;
import view.presenter.EventPresenter;
import view.presenter.PieceCountLine;

/**
 * Presents {@link BoardStateReported}: it writes the report at the end of a round: the piece count
 * of every player and every piece, blockade and effect.
 */
public final class BoardStateReportedPresenter extends EventPresenter<BoardStateReported> {

    private static final String BOARD_STATE_BANNER_BORDER = "=".repeat(37);

    // Reports list players in this order; the toss winner's turn order replaces it.
    private List<Player> playersInTurnOrder;
    private final Board board;
    private final BlockTravelDirectionStrategy blockTravelDirectionStrategy;
    private final CellNames cellNames;

    /**
     * Creates the presenter.
     *
     * @param allPlayers all players of the game; the report lists them in this order until a turn
     *     order is set
     * @param board the board the pieces move on
     * @param blockTravelDirectionStrategy the strategy that decides the direction of a blockade
     */
    public BoardStateReportedPresenter(
            List<Player> allPlayers, Board board, BlockTravelDirectionStrategy blockTravelDirectionStrategy) {
        super(BoardStateReported.class);
        this.playersInTurnOrder = allPlayers;
        this.board = board;
        this.blockTravelDirectionStrategy = blockTravelDirectionStrategy;
        this.cellNames = new CellNames(board);
    }

    /**
     * Sets the order in which the report lists the players.
     *
     * @param turnOrder the players in play order, starting with the winner of the toss
     */
    public void setTurnOrder(List<Player> turnOrder) {
        this.playersInTurnOrder = turnOrder;
    }

    @Override
    public String present(BoardStateReported message) {
        return describeBoardState(message.roundNumber());
    }

    private String describeBoardState(int roundNumber) {
        StringBuilder report = new StringBuilder();
        report.append(describeRoundStatusSummary());
        report.append("\nRound ").append(roundNumber).append(" Current Board State\n");
        report.append(BOARD_STATE_BANNER_BORDER).append('\n');

        List<String> playerRows = new ArrayList<>();
        for (Player player : playersInTurnOrder) {
            playerRows.add(describePlayerRow(player));
        }
        report.append(String.join("\n\n", playerRows));

        report.append('\n').append(BOARD_STATE_BANNER_BORDER);

        return report.toString();
    }

    // Requirement 5: a per-player board/base tally, printed before the board-state dump.
    private String describeRoundStatusSummary() {
        StringBuilder summary = new StringBuilder("\n-------------------------------\n");

        for (Player player : playersInTurnOrder) {
            summary.append(PieceCountLine.describe(
                            PlayerColorNames.displayNameOf(player.getColor()),
                            player.countPiecesOnBoard(), player.countPiecesAtBase()))
                    .append('\n');
        }

        summary.append("-------------------------------\n");

        return summary.toString();
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
                .filter(piece -> piece.getOriginalMovementDirection() == travelDirection)
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
    private static String describeDirection(Piece piece) {
        if (piece.isAtBase() || piece.isHome()) {
            return "";
        }

        MovementDirectionStrategy direction = piece.getMovementDirection();
        MovementDirectionStrategy originalDirection = piece.getOriginalMovementDirection();

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

        return cellNames.labelOf(piece.getTrackPosition());
    }
}
