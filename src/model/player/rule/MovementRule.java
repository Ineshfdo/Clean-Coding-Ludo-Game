package model.player.rule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import config.constant.BlockadeConstants;
import model.player.command.movement.BlockMoveCommand;
import model.player.command.cannotmove.BlockRollTooSmallCommand;
import model.player.command.movement.BreakBlockCommand;
import model.player.command.Command;
import model.player.command.cannotmove.ExactRollRequiredCommand;
import model.player.command.cannotmove.MoveBlockedByBlockadeCommand;
import model.player.command.movement.MoveCommand;
import model.player.command.cannotmove.SickRollTooSmallCommand;
import model.position.MovementDirectionStrategy;
import model.board.Board;
import model.player.strategy.BlockDirectionStrategy;
import model.effect.movement.MovementEffect;
import model.piece.Piece;
import model.player.Player;

// Rule 1: a track piece moves by the dice value, capped by any blockade (T-3).
public final class MovementRule implements TurnRule {

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

    // T-16: every distinct piece/block gets its own option, so a Strategy can genuinely
    // choose among them - not just react to whichever one a fixed scan order tried first.
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

    // Rule 10/T-4/T-12: a stalled piece needs the message matching why it cannot move.
    private Command buildNoMoveCommand(Piece representative, List<Piece> candidates, int rollValue) {
        if (representative.isOnHomeStraight()) {
            return new ExactRollRequiredCommand(representative);
        }
        List<Piece> blockPieces = findOwnBlock(representative, candidates);
        int blockAdjustedSteps = blockMovementRule.limitSteps(blockPieces, rollValue);
        if (blockAdjustedSteps == 0) {
            return new BlockRollTooSmallCommand(representative);
        }
        if (resolveActiveEffect(blockPieces).applyTo(blockAdjustedSteps) == 0) {
            return new SickRollTooSmallCommand(representative);
        }
        return new MoveBlockedByBlockadeCommand(blockPieces);
    }

    // T-3/T-1/T-5: a block moves together; a lone piece breaks away if a restore is owed.
    private Command buildMoveCommand(
            Player player, Piece piece, List<Piece> blockPieces, int effectiveSteps, Board board,
            MovementDirectionStrategy travelDirection) {
        if (blockPieces.size() >= BlockadeConstants.MINIMUM_BLOCKADE_SIZE) {
            return new BlockMoveCommand(
                    player, blockPieces, effectiveSteps, board, homeStraightEntryRule, travelDirection);
        }
        Command moveCommand = new MoveCommand(
                player, piece, effectiveSteps, board, homeStraightEntryRule, travelDirection);
        if (piece.hasAdoptedBlockDirection()) {
            return new BreakBlockCommand(player, List.of(piece), List.of(moveCommand));
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
        return blockDirectionStrategy.resolveTravelDirection(blockPieces, board);
    }

    // T-3/T-4/T-12/Rule 10: block-size division, then Energized/Sick, then blockade capping.
    private int effectiveSteps(
            Player player, Piece piece, int rollValue, Board board, List<Player> allPlayers,
            List<Piece> blockPieces, MovementDirectionStrategy travelDirection) {
        if (piece.isOnTrack()) {
            int requestedSteps = blockMovementRule.limitSteps(blockPieces, rollValue);
            int adjustedSteps = resolveActiveEffect(blockPieces).applyTo(requestedSteps);
            return blockadeRule.limitSteps(
                    player.getColor(), piece.getTrackPosition(), adjustedSteps, board, allPlayers,
                    travelDirection, blockPieces.size());
        }
        int adjustedRollValue = resolveActiveEffect(blockPieces).applyTo(rollValue);
        return exactHomeRule.forbidsMove(piece, adjustedRollValue) ? 0 : adjustedRollValue;
    }

    // T-12: a genuinely-assigned block effect (from teleporting to Alpha together) overrides
    // every member's own individual effect while grouped. A normal blockade formed by
    // ordinary movement never gets one, so it falls back to plain diceRoll/blockSize (T-4) -
    // individual Energized/Sick effects never affect blockade movement, and are never lost:
    // they stay stored on each piece and simply resume once it moves solo again.
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

    // T-13: a Beta-restricted piece/block is excluded entirely, the same as one at Base or Home.
    private static List<Piece> findMovableCandidates(Player player) {
        return player.getPieces().stream()
                .filter(piece -> piece.isOnTrack() || piece.isOnHomeStraight())
                .filter(piece -> !piece.getRestrictionState().forbidsMovement())
                .collect(Collectors.toList());
    }
}
