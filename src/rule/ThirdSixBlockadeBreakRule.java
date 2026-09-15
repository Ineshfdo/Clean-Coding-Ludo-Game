package rule;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import command.BreakBlockCommand;
import command.Command;
import command.MoveCommand;
import direction.MovementDirectionStrategy;
import ludoboard.Board;
import player.HomeStraightEntryRule;
import player.Piece;
import player.Player;

// T-6: a third consecutive six forces an existing blockade to break instead of being voided.
public final class ThirdSixBlockadeBreakRule extends BlockadeBreakRule {

    private static final int FORCING_ROLL_NUMBER = 3;
    private static final int FORCING_ROLL_VALUE = 6;
    private static final int FORCED_MOVE_STEPS = 6;
    private static final int BLOCKADE_PIECE_COUNT = 2;

    private final HomeStraightEntryRule homeStraightEntryRule;

    public ThirdSixBlockadeBreakRule(HomeStraightEntryRule homeStraightEntryRule) {
        this.homeStraightEntryRule = homeStraightEntryRule;
    }

    @Override
    protected Optional<Command> identify(
            Player player, int rollNumber, int rollValue, Board board, List<Player> allPlayers) {
        if (rollNumber != FORCING_ROLL_NUMBER || rollValue != FORCING_ROLL_VALUE) {
            return Optional.empty();
        }
        return findBlockade(player).map(blockadePieces -> buildBreakCommand(player, blockadePieces, board));
    }

    // T-6: the lowest-numbered member stays; the rest are released to move by their own direction.
    private Command buildBreakCommand(Player player, List<Piece> blockadePieces, Board board) {
        List<Piece> releasedPieces = blockadePieces.subList(1, blockadePieces.size());
        List<Command> releasedPieceMoves = releasedPieces.stream()
                .map(piece -> buildReleasedMove(player, piece, board))
                .collect(Collectors.toList());

        return new BreakBlockCommand(player, blockadePieces, releasedPieceMoves);
    }

    // T-6/T-5: each released piece moves 6 cells using its own original direction, not the block's.
    private Command buildReleasedMove(Player player, Piece piece, Board board) {
        MovementDirectionStrategy ownDirection = piece.getOriginalMovementDirectionStrategy();
        return new MoveCommand(
                player, piece, FORCED_MOVE_STEPS, board, homeStraightEntryRule, ownDirection);
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
            if (sharedCell.size() >= BLOCKADE_PIECE_COUNT) {
                return Optional.of(sharedCell);
            }
        }
        return Optional.empty();
    }
}
