package rule;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import command.BlockMoveCommand;
import command.BlockRollTooSmallCommand;
import command.BlockedMoveCommand;
import command.BreakBlockCommand;
import command.Command;
import command.ExactRollRequiredCommand;
import command.MoveCommand;
import direction.MovementDirectionStrategy;
import ludoboard.Board;
import player.BlockDirectionStrategy;
import player.ExactHomeRule;
import player.HomeStraightEntryRule;
import player.Piece;
import player.Player;

// Rule 1: a track piece moves by the dice value, capped by any blockade (T-3).
public final class MovementRule implements TurnRule {

    private static final int BLOCKADE_PIECE_COUNT = 2;

    private final BlockadeRule blockadeRule;
    private final HomeStraightEntryRule homeStraightEntryRule;
    private final ExactHomeRule exactHomeRule;
    private final BlockMovementRule blockMovementRule;
    private final BlockDirectionStrategy blockDirectionStrategy;

    public MovementRule(
            BlockadeRule blockadeRule, HomeStraightEntryRule homeStraightEntryRule,
            ExactHomeRule exactHomeRule, BlockMovementRule blockMovementRule,
            BlockDirectionStrategy blockDirectionStrategy) {
        this.blockadeRule = blockadeRule;
        this.homeStraightEntryRule = homeStraightEntryRule;
        this.exactHomeRule = exactHomeRule;
        this.blockMovementRule = blockMovementRule;
        this.blockDirectionStrategy = blockDirectionStrategy;
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
                    effectiveSteps(player, piece, rollValue, board, allPlayers, blockPieces, travelDirection);
            if (effectiveSteps > 0) {
                return Optional.of(buildMoveCommand(
                        player, piece, blockPieces, effectiveSteps, board, travelDirection));
            }
        }

        return Optional.of(buildNoMoveCommand(candidates.get(0), candidates, rollValue));
    }

    // Rule 10/T-4: a stalled piece needs the message matching why it cannot move.
    private Command buildNoMoveCommand(Piece representative, List<Piece> candidates, int rollValue) {
        if (representative.isOnHomeStraight()) {
            return new ExactRollRequiredCommand(representative);
        }
        List<Piece> blockPieces = findOwnBlock(representative, candidates);
        if (blockMovementRule.limitSteps(blockPieces, rollValue) == 0) {
            return new BlockRollTooSmallCommand(representative);
        }
        return new BlockedMoveCommand(representative);
    }

    // T-3/T-1/T-5: a block moves together; a lone piece breaks away if a restore is owed.
    private Command buildMoveCommand(
            Player player, Piece piece, List<Piece> blockPieces, int effectiveSteps, Board board,
            MovementDirectionStrategy travelDirection) {
        if (blockPieces.size() >= BLOCKADE_PIECE_COUNT) {
            return new BlockMoveCommand(
                    player, blockPieces, effectiveSteps, board, homeStraightEntryRule, travelDirection);
        }
        Command moveCommand = new MoveCommand(
                player, piece, effectiveSteps, board, homeStraightEntryRule, travelDirection);
        if (piece.hasAdoptedBlockDirection()) {
            return new BreakBlockCommand(player, piece, moveCommand);
        }
        return moveCommand;
    }

    // Rule 10: same-index HomeStraight pieces need the same roll, so they move as a block.
    private static List<Piece> findOwnBlock(Piece piece, List<Piece> candidates) {
        if (piece.isOnTrack()) {
            return candidates.stream()
                .filter(Piece::isOnTrack)
                .filter(candidate -> candidate.getTrackPosition() == piece.getTrackPosition())
                .collect(Collectors.toList());
        }
        return candidates.stream()
            .filter(Piece::isOnHomeStraight)
            .filter(candidate -> candidate.getHomeStraightIndex() == piece.getHomeStraightIndex())
            .collect(Collectors.toList());
    }

    // T-1: HomeStraight cells are single-color, so only track pieces need a block direction.
    private MovementDirectionStrategy resolveTravelDirection(
            Piece piece, List<Piece> blockPieces, Board board) {
        if (!piece.isOnTrack()) {
            return piece.getMovementDirectionStrategy();
        }
        return blockDirectionStrategy.resolveDominantPiece(blockPieces, board)
                .getOriginalMovementDirectionStrategy();
    }

    // T-3/T-4/Rule 10: a track block is first halved if mixed-direction, then capped by blockade.
    private int effectiveSteps(
            Player player, Piece piece, int rollValue, Board board, List<Player> allPlayers,
            List<Piece> blockPieces, MovementDirectionStrategy travelDirection) {
        if (piece.isOnTrack()) {
            int requestedSteps = blockMovementRule.limitSteps(blockPieces, rollValue);
            return blockadeRule.limitSteps(
                    player.getColor(), piece.getTrackPosition(), requestedSteps, board, allPlayers,
                    travelDirection);
        }
        return exactHomeRule.forbidsMove(piece, rollValue) ? 0 : rollValue;
    }

    private static List<Piece> findMovableCandidates(Player player) {
        return player.getPieces().stream()
                .filter(piece -> piece.isOnTrack() || piece.isOnHomeStraight())
                .collect(Collectors.toList());
    }
}
