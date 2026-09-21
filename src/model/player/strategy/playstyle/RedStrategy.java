package model.player.strategy.playstyle;

import config.enums.CommandType;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import model.direction.RemainingHomeDistance;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.strategy.PlayerStrategy;
import model.player.strategy.StrategyContext;
import model.player.strategy.helper.CaptureTargetFinder;

// Red is capture-focused: captures first, then leaves Base, keeps a piece on the path, and avoids forming blocks.
public final class RedStrategy implements PlayerStrategy {

    @Override
    public Command choose(List<Command> legalCommands, StrategyContext context) {
        Optional<Command> bestCapture = findBestCapture(legalCommands, context);

        if (bestCapture.isPresent()) {
            return bestCapture.get();
        }

        Optional<Command> enterBoardMove = findEnterBoardCommand(legalCommands, context);

        if (enterBoardMove.isPresent()) {
            return enterBoardMove.get();
        }

        return chooseMove(legalCommands, context);
    }

    // Rule (1): among captures, prefer the opponent piece closest to its own Home.
    private static Optional<Command> findBestCapture(List<Command> legalCommands, StrategyContext context) {
        return legalCommands.stream()
            .flatMap(command -> findCapture(command, context).stream())
            .min(Comparator.comparingInt(
                capture -> RemainingHomeDistance.forPiece(capture.capturedPiece, context.getBoard())))
            .map(capture -> capture.command);
    }

    // Delegates to the shared capture-aware finder.
    private static Optional<CaptureOption> findCapture(Command command, StrategyContext context) {
        return CaptureTargetFinder.findTarget(command, context)
            .map(capturedPiece -> new CaptureOption(command, capturedPiece));
    }

    // Rule (2): leave Base only when no capture is available, and not onto a block.
    private static Optional<Command> findEnterBoardCommand(List<Command> legalCommands, StrategyContext context) {
        return legalCommands.stream()
            .filter(command -> command.getType() == CommandType.ENTER_BOARD)
            .filter(command -> !formsNewBlock(command, context))
            .findFirst();
    }

    // Rules (3)/(4): keep a piece on the path and form no block; each is dropped only if unavoidable.
    private static Command chooseMove(List<Command> legalCommands, StrategyContext context) {
        List<Command> moves = legalCommands.stream()
            .filter(command -> command.getType() != CommandType.CANNOT_MOVE)
            .collect(Collectors.toList());

        if (moves.isEmpty()) {
            return legalCommands.get(0);
        }

        return findFirst(moves, command -> keepsPieceOnPath(command, context) && !formsNewBlock(command, context))
            .or(() -> findFirst(moves, command -> keepsPieceOnPath(command, context)))
            .or(() -> findFirst(moves, command -> !formsNewBlock(command, context)))
            .orElse(moves.get(0));
    }

    // Rule (4): a move may take a piece off the path only if another Red piece stays on it.
    private static boolean keepsPieceOnPath(Command command, StrategyContext context) {
        if (!command.leavesStandardPath()) {
            return true;
        }

        List<Piece> movingPieces = findMovingPieces(command, context.getPlayer());

        return context.getPlayer().getPieces().stream()
            .filter(Piece::isOnTrack)
            .anyMatch(piece -> !movingPieces.contains(piece));
    }

    // A block moves together, but its command only names the first member.
    private static List<Piece> findMovingPieces(Command command, Player player) {
        if (!command.movesExistingBlock()) {
            return command.getAffectedPieces();
        }

        int blockPosition = command.getAffectedPiece().getTrackPosition();

        return player.getPieces().stream()
            .filter(Piece::isOnTrack)
            .filter(piece -> piece.getTrackPosition() == blockPosition)
            .collect(Collectors.toList());
    }

    private static boolean formsNewBlock(Command command, StrategyContext context) {
        return findLandingPosition(command, context)
            .map(landingPosition -> countOwnPiecesAt(context.getPlayer(), landingPosition) > 0)
            .orElse(false);
    }

    // Entering has no previewed landing, but it always lands on X.
    private static Optional<Integer> findLandingPosition(Command command, StrategyContext context) {
        if (command.getType() == CommandType.ENTER_BOARD) {
            return Optional.of(context.getBoard().getEntryCellPosition(context.getPlayer().getColor()));
        }

        return command.previewLandingPosition();
    }

    private static int countOwnPiecesAt(Player player, int trackPosition) {
        return (int) player.getPieces().stream()
            .filter(Piece::isOnTrack)
            .filter(piece -> piece.getTrackPosition() == trackPosition)
            .count();
    }

    private static Optional<Command> findFirst(List<Command> commands, Predicate<Command> condition) {
        return commands.stream().filter(condition).findFirst();
    }

    // Pairs a candidate move with the opponent piece it would capture.
    private static final class CaptureOption {
        private final Command command;
        private final Piece capturedPiece;

        private CaptureOption(Command command, Piece capturedPiece) {
            this.command = command;
            this.capturedPiece = capturedPiece;
        }
    }
}
