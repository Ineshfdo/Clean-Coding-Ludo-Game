package model.player.rule;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import config.constant.BlockadeConstants;
import config.constant.DiceConstants;
import model.player.command.move.BreakBlockCommand;
import model.player.command.Command;
import model.player.command.move.MovePieceCommand;
import model.position.MovementDirectionStrategy;
import model.board.Board;
import model.piece.Piece;
import model.player.Player;

// T-6: a third consecutive six forces an existing blockade to break instead of being voided.
public final class ThirdSixBlockadeBreakRule extends BlockadeBreakRule {

    private static final int TOTAL_FORCED_MOVE_STEPS = 6;

    private final HomeStraightEntryRule homeStraightEntryRule;

    public ThirdSixBlockadeBreakRule(HomeStraightEntryRule homeStraightEntryRule) {
        this.homeStraightEntryRule = homeStraightEntryRule;
    }

    @Override
    protected Optional<Command> identify(
            Player player, int consecutiveSixCount, int rollValue, Board board, List<Player> allPlayers) {
        if (consecutiveSixCount != DiceConstants.THIRD_CONSECUTIVE_SIX_COUNT
                || rollValue != DiceConstants.SIX_ROLL_VALUE) {
            return Optional.empty();
        }
        return findBlockade(player).map(blockadePieces -> buildBreakCommand(player, blockadePieces, board));
    }

    // T-6: the lowest-numbered member stays; the rest are released, sharing the 6 cells
    // between them, and move by their own direction.
    private Command buildBreakCommand(Player player, List<Piece> blockadePieces, Board board) {
        List<Piece> releasedPieces = blockadePieces.subList(1, blockadePieces.size());
        int stepsPerReleasedPiece = TOTAL_FORCED_MOVE_STEPS / releasedPieces.size();
        List<Command> releasedPieceMoves = releasedPieces.stream()
                .map(piece -> buildReleasedMove(player, piece, board, stepsPerReleasedPiece))
                .collect(Collectors.toList());

        return new BreakBlockCommand(player, blockadePieces, releasedPieceMoves);
    }

    // T-6/T-5: each released piece moves its share of the 6 cells using its own original
    // direction, not the block's.
    private Command buildReleasedMove(Player player, Piece piece, Board board, int steps) {
        MovementDirectionStrategy ownDirection = piece.getOriginalMovementDirectionStrategy();
        return new MovePieceCommand(
                player, piece, steps, board, homeStraightEntryRule, ownDirection);
    }

    // T-3: a blockade is 2+ of the player's own pieces sharing a track cell.
    private static Optional<List<Piece>> findBlockade(Player player) {
        List<Piece> trackPieces = player.getPieces().stream()
                .filter(Piece::isOnTrack)
                .collect(Collectors.toList());

        for (Piece piece : trackPieces) {
            List<Piece> sharedCell = trackPieces.stream()
                    .filter(candidate -> candidate.getTrackPosition() == piece.getTrackPosition())
                    .collect(Collectors.toList());
            if (sharedCell.size() >= BlockadeConstants.MINIMUM_BLOCKADE_SIZE) {
                return Optional.of(sharedCell);
            }
        }
        return Optional.empty();
    }
}
