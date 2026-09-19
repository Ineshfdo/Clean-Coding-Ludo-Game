package model.player.rule.turn;

import config.constant.BlockadeConstants;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import model.board.Board;
import model.effect.movement.MovementEffect;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.command.cannotmove.BlockRollTooSmallCommand;
import model.player.command.cannotmove.ExactRollRequiredCommand;
import model.player.command.cannotmove.MoveBlockedByBlockadeCommand;
import model.player.command.cannotmove.SickRollTooSmallCommand;
import model.player.command.move.BreakBlockCommand;
import model.player.command.move.MoveBlockCommand;
import model.player.command.move.MovePieceCommand;
import model.player.rule.block.BlockStepsRule;
import model.player.rule.block.BlockadeLimitRule;
import model.player.rule.home.ExactRollRule;
import model.player.rule.home.HomeStraightEntryRule;
import model.player.strategy.BlockDirectionStrategy;
import model.position.MovementDirectionStrategy;

// Rule 1: moves a piece by the dice value, capped by blockades (T-3).
public final class MovePiecesRule implements TurnRule {

    private final BlockadeLimitRule blockadeLimitRule;
    private final HomeStraightEntryRule homeStraightEntryRule;
    private final ExactRollRule exactRollRule;
    private final BlockStepsRule blockStepsRule;
    private final BlockDirectionStrategy blockDirectionStrategy;

    public MovePiecesRule(
            BlockadeLimitRule blockadeLimitRule, HomeStraightEntryRule homeStraightEntryRule,
            ExactRollRule exactRollRule, BlockStepsRule blockStepsRule,
            BlockDirectionStrategy blockDirectionStrategy) {
        this.blockadeLimitRule = blockadeLimitRule;
        this.homeStraightEntryRule = homeStraightEntryRule;
        this.exactRollRule = exactRollRule;
        this.blockStepsRule = blockStepsRule;
        this.blockDirectionStrategy = blockDirectionStrategy;
    }

    // T-16: every distinct piece or block gets its own option.
    @Override
    public List<Command> resolve(
            Player player, int rollValue, Board board, List<Player> allPlayers) {
        List<Piece> candidates = findMovableCandidates(player);

        if (candidates.isEmpty()) {
            return List.of();
        }

        List<Command> moveOptions = new ArrayList<>();
        Set<Piece> coveredPieces = new HashSet<>();

        for (Piece piece : candidates) {
            if (coveredPieces.contains(piece)) {
                continue;
            }

            List<Piece> blockPieces = findOwnBlock(piece, candidates);
            coveredPieces.addAll(blockPieces);

            MovementDirectionStrategy travelDirection = resolveTravelDirection(piece, blockPieces, board);
            int effectiveSteps =
                effectiveSteps(player, piece, rollValue, board, allPlayers, blockPieces, travelDirection);

            if (effectiveSteps > 0) {
                moveOptions.add(buildMoveCommand(
                    player, piece, blockPieces, effectiveSteps, board, travelDirection));
            }
        }

        if (moveOptions.isEmpty()) {
            return List.of(buildNoMoveCommand(candidates.get(0), candidates, rollValue));
        }

        return moveOptions;
    }

    // Rule 10/T-4/T-12: picks the message matching why the piece can't move.
    private Command buildNoMoveCommand(Piece representative, List<Piece> candidates, int rollValue) {
        if (representative.isOnHomeStraight()) {
            return new ExactRollRequiredCommand(representative);
        }

        List<Piece> blockPieces = findOwnBlock(representative, candidates);
        int blockAdjustedSteps = blockStepsRule.limitSteps(blockPieces, rollValue);

        if (blockAdjustedSteps == 0) {
            return new BlockRollTooSmallCommand(representative);
        }

        if (resolveActiveEffect(blockPieces).applyTo(blockAdjustedSteps) == 0) {
            return new SickRollTooSmallCommand(representative);
        }

        return new MoveBlockedByBlockadeCommand(blockPieces);
    }

    // T-3/T-1/T-5: a block moves together; a lone piece breaks away if owed.
    private Command buildMoveCommand(
            Player player, Piece piece, List<Piece> blockPieces, int effectiveSteps, Board board,
            MovementDirectionStrategy travelDirection) {
        if (blockPieces.size() >= BlockadeConstants.MINIMUM_BLOCKADE_SIZE) {
            return new MoveBlockCommand(
                player, blockPieces, effectiveSteps, board, homeStraightEntryRule, travelDirection);
        }

        Command moveCommand = new MovePieceCommand(
                player, piece, effectiveSteps, board, homeStraightEntryRule, travelDirection);

        if (piece.hasAdoptedBlockDirection()) {
            return new BreakBlockCommand(player, List.of(piece), List.of(moveCommand));
        }

        return moveCommand;
    }

    // Rule 10: same-index HomeStraight pieces move as a block.
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

    // T-1: only track pieces need a block direction.
    private MovementDirectionStrategy resolveTravelDirection(
            Piece piece, List<Piece> blockPieces, Board board) {
        if (!piece.isOnTrack()) {
            return piece.getMovementDirectionStrategy();
        }

        return blockDirectionStrategy.resolveTravelDirection(blockPieces, board);
    }

    // T-3/T-4/T-12/Rule 10: block division, then Energized/Sick, then blockade cap.
    private int effectiveSteps(
            Player player, Piece piece, int rollValue, Board board, List<Player> allPlayers,
            List<Piece> blockPieces, MovementDirectionStrategy travelDirection) {
        if (piece.isOnTrack()) {
            int requestedSteps = blockStepsRule.limitSteps(blockPieces, rollValue);
            int adjustedSteps = resolveActiveEffect(blockPieces).applyTo(requestedSteps);

            return blockadeLimitRule.limitSteps(
                player.getColor(), piece.getTrackPosition(), adjustedSteps, board, allPlayers,
                travelDirection, blockPieces.size());
        }

        int adjustedRollValue = resolveActiveEffect(blockPieces).applyTo(rollValue);

        return exactRollRule.forbidsMove(piece, adjustedRollValue) ? 0 : adjustedRollValue;
    }

    // T-12: a block effect (from Alpha) overrides members' own effects while grouped.
    // Individual effects are kept and resume once a piece moves solo.
    private static MovementEffect resolveActiveEffect(List<Piece> blockPieces) {
        Piece representative = blockPieces.get(0);
        int currentBlockSize = blockPieces.size();

        if (currentBlockSize < BlockadeConstants.MINIMUM_BLOCKADE_SIZE) {
            return representative.getIndividualEffect();
        }

        return representative.hasActiveBlockEffectForSize(currentBlockSize)
            ? representative.getBlockEffect()
            : MovementEffect.none();
    }

    // T-13: Beta-restricted pieces are excluded, like those at Base or Home.
    private static List<Piece> findMovableCandidates(Player player) {
        return player.getPieces().stream()
            .filter(piece -> piece.isOnTrack() || piece.isOnHomeStraight())
            .filter(piece -> !piece.getRestrictionState().forbidsMovement())
            .collect(Collectors.toList());
    }
}
