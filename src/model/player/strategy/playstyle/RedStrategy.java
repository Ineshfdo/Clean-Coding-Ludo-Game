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
    public Command choose(List<Command> legalOptions, StrategyContext context) {
        Optional<Command> bestCapture = findBestCapture(legalOptions, context);

        if (bestCapture.isPresent()) {
            return bestCapture.get();
        }

        Optional<Command> enterFromBase = findEnterBoardOption(legalOptions, context);

        if (enterFromBase.isPresent()) {
            return enterFromBase.get();
        }

        return chooseMove(legalOptions, context);
    }

    // Rule (1): among captures, prefer the opponent piece closest to its own Home.
    private static Optional<Command> findBestCapture(List<Command> legalOptions, StrategyContext context) {
        return legalOptions.stream()
            .flatMap(option -> findCapture(option, context).stream())
            .min(Comparator.comparingInt(
                capture -> RemainingHomeDistance.forPiece(capture.capturedPiece, context.getBoard())))
            .map(capture -> capture.option);
    }

    // Delegates to the shared capture-aware finder.
    private static Optional<Capture> findCapture(Command option, StrategyContext context) {
        return CaptureTargetFinder.findTarget(option, context)
            .map(capturedPiece -> new Capture(option, capturedPiece));
    }

    // Rule (2): leave Base only when no capture is available, and not onto a block.
    private static Optional<Command> findEnterBoardOption(List<Command> legalOptions, StrategyContext context) {
        return legalOptions.stream()
            .filter(option -> option.getType() == CommandType.ENTER_BOARD)
            .filter(option -> !formsNewBlock(option, context))
            .findFirst();
    }

    // Rules (3)/(4): keep a piece on the path and form no block; each is dropped only if unavoidable.
    private static Command chooseMove(List<Command> legalOptions, StrategyContext context) {
        List<Command> moves = legalOptions.stream()
            .filter(option -> option.getType() != CommandType.CANNOT_MOVE)
            .collect(Collectors.toList());

        if (moves.isEmpty()) {
            return legalOptions.get(0);
        }

        return findFirst(moves, option -> keepsPieceOnPath(option, context) && !formsNewBlock(option, context))
            .or(() -> findFirst(moves, option -> keepsPieceOnPath(option, context)))
            .or(() -> findFirst(moves, option -> !formsNewBlock(option, context)))
            .orElse(moves.get(0));
    }

    // Rule (4): a move may take a piece off the path only if another Red piece stays on it.
    private static boolean keepsPieceOnPath(Command option, StrategyContext context) {
        if (!option.leavesStandardPath()) {
            return true;
        }

        List<Piece> movingPieces = findMovingPieces(option, context.getPlayer());

        return context.getPlayer().getPieces().stream()
            .filter(Piece::isOnTrack)
            .anyMatch(piece -> !movingPieces.contains(piece));
    }

    // A block moves together, but its command only names the first member.
    private static List<Piece> findMovingPieces(Command option, Player player) {
        if (!option.movesExistingBlock()) {
            return option.getAffectedPieces();
        }

        int blockPosition = option.getAffectedPiece().getTrackPosition();

        return player.getPieces().stream()
            .filter(Piece::isOnTrack)
            .filter(piece -> piece.getTrackPosition() == blockPosition)
            .collect(Collectors.toList());
    }

    private static boolean formsNewBlock(Command option, StrategyContext context) {
        return findLandingPosition(option, context)
            .map(landingPosition -> countOwnPiecesAt(context.getPlayer(), landingPosition) > 0)
            .orElse(false);
    }

    // Entering has no previewed landing, but it always lands on X.
    private static Optional<Integer> findLandingPosition(Command option, StrategyContext context) {
        if (option.getType() == CommandType.ENTER_BOARD) {
            return Optional.of(context.getBoard().getEntryCellPosition(context.getPlayer().getColor()));
        }

        return option.previewLandingPosition();
    }

    private static int countOwnPiecesAt(Player player, int trackPosition) {
        return (int) player.getPieces().stream()
            .filter(Piece::isOnTrack)
            .filter(piece -> piece.getTrackPosition() == trackPosition)
            .count();
    }

    private static Optional<Command> findFirst(List<Command> options, Predicate<Command> condition) {
        return options.stream().filter(condition).findFirst();
    }

    // Pairs a candidate move with the opponent piece it would capture.
    private static final class Capture {
        private final Command option;
        private final Piece capturedPiece;

        private Capture(Command option, Piece capturedPiece) {
            this.option = option;
            this.capturedPiece = capturedPiece;
        }
    }
}
