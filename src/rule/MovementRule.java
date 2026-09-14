package rule;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import command.BlockMoveCommand;
import command.BlockedMoveCommand;
import command.Command;
import command.MoveCommand;
import ludoboard.Board;
import player.Piece;
import player.Player;

// Rule 1: a piece already on the track moves forward by the
// dice's value, capped by any blockade in its path (T-3).
public final class MovementRule implements TurnRule {

    private static final int BLOCKADE_PIECE_COUNT = 2;

    private final BlockadeRule blockadeRule;

    public MovementRule(BlockadeRule blockadeRule) {
        this.blockadeRule = blockadeRule;
    }

    @Override
    public Optional<Command> resolve(
            Player player, int rollValue, Board board, List<Player> allPlayers) {
        List<Piece> candidates = findMovableCandidates(player);
        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        // Try this player's own pieces in order; a blockade against
        // one piece must not stop a different, unblocked piece from
        // moving with the same roll.
        for (Piece piece : candidates) {
            int effectiveSteps = effectiveSteps(player, piece, rollValue, board, allPlayers);
            if (effectiveSteps > 0) {
                return Optional.of(buildMoveCommand(player, piece, candidates, effectiveSteps, board));
            }
        }

        return Optional.of(new BlockedMoveCommand(candidates.get(0)));
    }

    // T-3: a piece sharing its own color's block moves together with
    // its partner(s), using the same effective steps.
    private static Command buildMoveCommand(
            Player player, Piece piece, List<Piece> candidates, int effectiveSteps, Board board) {
        List<Piece> blockPieces = findOwnBlock(piece, candidates);
        if (blockPieces.size() >= BLOCKADE_PIECE_COUNT) {
            return new BlockMoveCommand(player, blockPieces, effectiveSteps, board);
        }
        return new MoveCommand(player, piece, effectiveSteps, board);
    }

    private static List<Piece> findOwnBlock(Piece piece, List<Piece> candidates) {
        if (!piece.isOnTrack()) {
            return List.of(piece);
        }
        return candidates.stream()
                .filter(Piece::isOnTrack)
                .filter(candidate -> candidate.getTrackPosition() == piece.getTrackPosition())
                .collect(Collectors.toList());
    }

    // T-3: HomeStraight cells are single-color, so a blockade can
    // only ever limit movement still on the shared track.
    private int effectiveSteps(
            Player player, Piece piece, int rollValue, Board board, List<Player> allPlayers) {
        if (!piece.isOnTrack()) {
            return rollValue;
        }
        return blockadeRule.limitSteps(
                player.getColor(), piece.getTrackPosition(), rollValue, board, allPlayers);
    }

    private static List<Piece> findMovableCandidates(Player player) {
        return player.getPieces().stream()
                .filter(piece -> piece.isOnTrack() || piece.isOnHomeStraight())
                .collect(Collectors.toList());
    }
}
