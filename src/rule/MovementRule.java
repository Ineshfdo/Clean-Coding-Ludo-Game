package rule;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import command.BlockMoveCommand;
import command.BlockedMoveCommand;
import command.Command;
import command.MoveCommand;
import direction.MovementDirectionStrategy;
import ludoboard.Board;
import player.BlockTravelDirection;
import player.HomeStraightEntryRule;
import player.Piece;
import player.Player;

// Rule 1: a track piece moves by the dice value, capped by any blockade (T-3).
public final class MovementRule implements TurnRule {

    private static final int BLOCKADE_PIECE_COUNT = 2;

    private final BlockadeRule blockadeRule;
    private final HomeStraightEntryRule homeStraightEntryRule;

    public MovementRule(BlockadeRule blockadeRule, HomeStraightEntryRule homeStraightEntryRule) {
        this.blockadeRule = blockadeRule;
        this.homeStraightEntryRule = homeStraightEntryRule;
    }

    @Override
    public Optional<Command> resolve(
            Player player, int rollValue, Board board, List<Player> allPlayers) {
        List<Piece> candidates = findMovableCandidates(player);
        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        // Try this player's pieces in order; a blockade on one must not stop another.
        for (Piece piece : candidates) {
            List<Piece> blockPieces = findOwnBlock(piece, candidates);
            MovementDirectionStrategy travelDirection = resolveTravelDirection(piece, blockPieces, board);
            int effectiveSteps =
                    effectiveSteps(player, piece, rollValue, board, allPlayers, travelDirection);
            if (effectiveSteps > 0) {
                return Optional.of(buildMoveCommand(
                        player, piece, blockPieces, effectiveSteps, board, travelDirection));
            }
        }

        return Optional.of(new BlockedMoveCommand(candidates.get(0)));
    }

    // T-3/T-1: pieces sharing a cell move together via the block's travelDirection.
    private Command buildMoveCommand(
            Player player, Piece piece, List<Piece> blockPieces, int effectiveSteps, Board board,
            MovementDirectionStrategy travelDirection) {
        if (blockPieces.size() >= BLOCKADE_PIECE_COUNT) {
            return new BlockMoveCommand(
                    player, blockPieces, effectiveSteps, board, homeStraightEntryRule, travelDirection);
        }
        return new MoveCommand(
                player, piece, effectiveSteps, board, homeStraightEntryRule, travelDirection);
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

    // T-1: HomeStraight cells are single-color, so only track pieces need a block direction.
    private static MovementDirectionStrategy resolveTravelDirection(
            Piece piece, List<Piece> blockPieces, Board board) {
        if (!piece.isOnTrack()) {
            return piece.getMovementDirectionStrategy();
        }
        return BlockTravelDirection.resolve(blockPieces, board);
    }

    // T-3: HomeStraight cells are single-color, so a blockade can only limit track movement.
    private int effectiveSteps(
            Player player, Piece piece, int rollValue, Board board, List<Player> allPlayers,
            MovementDirectionStrategy travelDirection) {
        if (!piece.isOnTrack()) {
            return rollValue;
        }
        return blockadeRule.limitSteps(
                player.getColor(), piece.getTrackPosition(), rollValue, board, allPlayers,
                travelDirection);
    }

    private static List<Piece> findMovableCandidates(Player player) {
        return player.getPieces().stream()
                .filter(piece -> piece.isOnTrack() || piece.isOnHomeStraight())
                .collect(Collectors.toList());
    }
}
