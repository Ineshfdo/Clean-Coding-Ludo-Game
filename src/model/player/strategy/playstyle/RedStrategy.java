package model.player.strategy.playstyle;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import model.piece.Piece;
import model.piece.RemainingHomeDistance;
import model.player.Player;
import model.player.command.MoveCommand;
import model.player.strategy.PlayerStrategy;
import model.player.strategy.StrategyContext;
import model.player.strategy.helper.CaptureTargetFinder;
import model.player.strategy.helper.CommandFinder;

/**
 Red is capture-focused.
 It captures first, and prefers the opponent piece that is closest to its own Home.
 Then it leaves Base, keeps at least one piece on the shared track, and avoids forming new blockades.
 */
public final class RedStrategy implements PlayerStrategy {

    @Override
    public MoveCommand choose(List<MoveCommand> legalCommands, StrategyContext context) {
        Optional<MoveCommand> bestCapture = findBestCapture(legalCommands, context);

        if (bestCapture.isPresent()) {
            return bestCapture.get();
        }

        Optional<MoveCommand> enterBoardMove = findEnterBoardCommand(legalCommands, context);

        if (enterBoardMove.isPresent()) {
            return enterBoardMove.get();
        }

        return chooseMove(legalCommands, context);
    }

    // Rule (1): among captures, prefer the opponent piece closest to its own Home.
    private static Optional<MoveCommand> findBestCapture(List<MoveCommand> legalCommands, StrategyContext context) {
        return legalCommands.stream()
            .flatMap(command -> findCapture(command, context).stream())
            .min(Comparator.comparingInt(
                capture -> RemainingHomeDistance.forPiece(capture.capturedPiece, context.getBoard())))
            .map(capture -> capture.command);
    }

    // Delegates to the shared capture-aware finder.
    private static Optional<CaptureOption> findCapture(MoveCommand command, StrategyContext context) {
        return CaptureTargetFinder.findTarget(command, context)
            .map(capturedPiece -> new CaptureOption(command, capturedPiece));
    }

    // Rule (2): leave Base only when no capture is available, and not onto a block.
    private static Optional<MoveCommand> findEnterBoardCommand(List<MoveCommand> legalCommands, StrategyContext context) {
        return legalCommands.stream()
            .filter(command -> command.entersBoard())
            .filter(command -> !formsNewBlock(command, context))
            .findFirst();
    }

    // Rules (3)/(4): keep a piece on the path and form no block; each is dropped only if unavoidable.
    private static MoveCommand chooseMove(List<MoveCommand> legalCommands, StrategyContext context) {
        List<MoveCommand> moves = legalCommands.stream()
            .filter(command -> !command.movesNothing())
            .collect(Collectors.toList());

        if (moves.isEmpty()) {
            return legalCommands.get(0);
        }

        return CommandFinder.findFirst(moves, command -> keepsPieceOnPath(command, context) && !formsNewBlock(command, context))
            .or(() -> CommandFinder.findFirst(moves, command -> keepsPieceOnPath(command, context)))
            .or(() -> CommandFinder.findFirst(moves, command -> !formsNewBlock(command, context)))
            .orElse(moves.get(0));
    }

    // Rule (4): a move may take a piece off the path only if another Red piece stays on it.
    private static boolean keepsPieceOnPath(MoveCommand command, StrategyContext context) {
        if (!command.leavesStandardPath()) {
            return true;
        }

        List<Piece> movingPieces = findMovingPieces(command, context.getPlayer());

        return context.getPlayer().getPieces().stream()
            .filter(Piece::isOnTrack)
            .anyMatch(piece -> !movingPieces.contains(piece));
    }

    // A block moves together, but its command only names the first member.
    private static List<Piece> findMovingPieces(MoveCommand command, Player player) {
        if (!command.movesExistingBlock()) {
            return command.getAffectedPieces();
        }

        int blockPosition = command.getAffectedPiece().getTrackPosition();

        return player.getPiecesAt(blockPosition);
    }

    private static boolean formsNewBlock(MoveCommand command, StrategyContext context) {
        return findLandingPosition(command, context)
            .map(landingPosition -> !context.getPlayer().getPiecesAt(landingPosition).isEmpty())
            .orElse(false);
    }

    // Entering has no previewed landing, but it always lands on X.
    private static Optional<Integer> findLandingPosition(MoveCommand command, StrategyContext context) {
        if (command.entersBoard()) {
            return Optional.of(context.getBoard().getEntryCellPosition(context.getPlayer().getColor()));
        }

        return command.previewLandingPosition();
    }

    // Pairs a candidate move with the opponent piece it would capture.
    private static final class CaptureOption {
        private final MoveCommand command;
        private final Piece capturedPiece;

        private CaptureOption(MoveCommand command, Piece capturedPiece) {
            this.command = command;
            this.capturedPiece = capturedPiece;
        }
    }
}
