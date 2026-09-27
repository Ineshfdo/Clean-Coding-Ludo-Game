package model.player.rule.block;

import config.constant.BlockadeConstants;
import config.constant.DiceConstants;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import model.board.Board;
import model.direction.MovementDirectionStrategy;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.command.MoveCommand;
import model.player.command.move.BreakBlockCommand;
import model.player.command.move.MovePieceCommand;
import model.player.rule.home.HomeStraightEntryRule;

/**
 * A third six in a row breaks an existing blockade instead of voiding the roll (T-6). The lowest-
 * numbered piece stays and the other pieces share the six cells.
 */
public final class ThirdSixBlockadeBreakRule extends BlockadeBreakRule {

    private static final int RELEASED_PIECE_BLOCK_SIZE = 1;
    private static final int STAYING_MEMBER_COUNT = 1;

    private final HomeStraightEntryRule homeStraightEntryRule;
    private final BlockadeLimitRule blockadeLimitRule;

    /**
     * Creates the rule.
     *
     * @param homeStraightEntryRule decides whether a released piece may enter its HomeStraight
     * @param blockadeLimitRule limits the steps of a released piece in front of an opponent
     *     blockade
     */
    public ThirdSixBlockadeBreakRule(
            HomeStraightEntryRule homeStraightEntryRule, BlockadeLimitRule blockadeLimitRule) {
        this.homeStraightEntryRule = homeStraightEntryRule;
        this.blockadeLimitRule = blockadeLimitRule;
    }

    @Override
    protected Optional<Command> identify(
            Player player, int consecutiveSixCount, int rollValue, Board board, List<Player> allPlayers) {
        if (consecutiveSixCount != DiceConstants.THIRD_CONSECUTIVE_SIX_COUNT
                || rollValue != DiceConstants.SIX_ROLL_VALUE) {
            return Optional.empty();
        }

        return findBlockade(player)
                .map(blockadePieces -> buildBreakCommand(player, blockadePieces, board, allPlayers));
    }

    // T-6: the lowest-numbered member stays; the rest share the 6 cells.
    private Command buildBreakCommand(
            Player player, List<Piece> blockadePieces, Board board, List<Player> allPlayers) {
        List<Piece> releasedPieces = blockadePieces.subList(STAYING_MEMBER_COUNT, blockadePieces.size());
        int stepsPerReleasedPiece = DiceConstants.SIX_ROLL_VALUE / releasedPieces.size();
        List<MoveCommand> releasedPieceMoves = releasedPieces.stream()
                .map(piece -> buildReleasedMove(player, piece, board, allPlayers, stepsPerReleasedPiece))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());

        return new BreakBlockCommand(player, blockadePieces, releasedPieceMoves);
    }

    // T-6/T-5: each released piece moves by its own original direction. T-3: it stops next to
    // an opponent block like any other move, and stays put if it can't move at all.
    private Optional<MoveCommand> buildReleasedMove(
            Player player, Piece piece, Board board, List<Player> allPlayers, int steps) {
        MovementDirectionStrategy ownDirection = piece.getOriginalMovementDirection();
        int allowedSteps = blockadeLimitRule.limitSteps(
                player.getColor(), piece.getTrackPosition(), steps, board, allPlayers, ownDirection,
                RELEASED_PIECE_BLOCK_SIZE);

        if (allowedSteps == 0) {
            return Optional.empty();
        }

        return Optional.of(new MovePieceCommand(
                player, piece, allowedSteps, board, homeStraightEntryRule, ownDirection));
    }

    // T-3: a blockade is 2+ own pieces sharing a track cell.
    private static Optional<List<Piece>> findBlockade(Player player) {
        for (Piece piece : player.getPieces()) {
            if (!piece.isOnTrack()) {
                continue;
            }

            List<Piece> piecesOnSameCell = player.getPiecesAt(piece.getTrackPosition());

            if (piecesOnSameCell.size() >= BlockadeConstants.MINIMUM_BLOCKADE_SIZE) {
                return Optional.of(piecesOnSameCell);
            }
        }

        return Optional.empty();
    }
}
