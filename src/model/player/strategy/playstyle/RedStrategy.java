package model.player.strategy.playstyle;

import config.enums.CommandType;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import model.board.Board;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.strategy.PlayerStrategy;
import model.player.strategy.StrategyContext;
import model.player.strategy.helper.CaptureTargetFinder;

// Red is capture-focused: captures first, then leaves Base, and avoids forming blocks.
public final class RedStrategy implements PlayerStrategy {

    @Override
    public Command choose(List<Command> legalOptions, StrategyContext context) {
        Optional<Command> bestCapture = findBestCapture(legalOptions, context);

        if (bestCapture.isPresent()) {
            return bestCapture.get();
        }

        Optional<Command> enterFromBase = findEnterBoardOption(legalOptions);

        if (enterFromBase.isPresent()) {
            return enterFromBase.get();
        }

        return findNonBlockFormingMove(legalOptions, context).orElse(legalOptions.get(0));
    }

    // Rule (1): among captures, prefer the opponent piece closest to its own Home.
    private static Optional<Command> findBestCapture(List<Command> legalOptions, StrategyContext context) {
        return legalOptions.stream()
            .flatMap(option -> findCapture(option, context).stream())
            .min(Comparator.comparingInt(capture -> distanceToOwnHome(capture.capturedPiece, context.getBoard())))
            .map(capture -> capture.option);
    }

    // Delegates to the shared capture-aware finder.
    private static Optional<Capture> findCapture(Command option, StrategyContext context) {
        return CaptureTargetFinder.findTarget(option, context)
            .map(capturedPiece -> new Capture(option, capturedPiece));
    }

    private static int distanceToOwnHome(Piece opponentPiece, Board board) {
        return board.getForwardDistance(
            opponentPiece.getTrackPosition(), board.getApproachCellPosition(opponentPiece.getColor()));
    }

    // Rule (2): leave Base only when no capture is available.
    private static Optional<Command> findEnterBoardOption(List<Command> legalOptions) {
        return legalOptions.stream()
            .filter(option -> option.getType() == CommandType.ENTER_BOARD)
            .findFirst();
    }

    // Rule (3): prefer a move that doesn't land on a Red piece and form a block.
    private static Optional<Command> findNonBlockFormingMove(
            List<Command> legalOptions, StrategyContext context) {
        return legalOptions.stream()
            .filter(option -> !formsNewBlock(option, context))
            .findFirst();
    }

    private static boolean formsNewBlock(Command option, StrategyContext context) {
        return option.previewLandingPosition()
            .map(landingPosition -> countOwnPiecesAt(context.getPlayer(), landingPosition) > 0)
            .orElse(false);
    }

    private static int countOwnPiecesAt(Player player, int trackPosition) {
        return (int) player.getPieces().stream()
            .filter(Piece::isOnTrack)
            .filter(piece -> piece.getTrackPosition() == trackPosition)
            .count();
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
