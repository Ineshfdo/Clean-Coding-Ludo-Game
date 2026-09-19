package model.player.strategy;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import model.player.command.Command;
import config.enums.CommandType;
import model.board.Board;
import model.piece.Piece;
import model.player.Player;

// T-16: Red is an aggressive, capture-focused player. It prioritizes capturing an opponent
// piece over any other move - preferring, among several available captures, the opponent
// piece closest to ITS OWN Home (the most advanced, most valuable piece to send back to
// Base) - only brings a new piece out of Base once no capture is available, and otherwise
// avoids landing on its own pieces to form a new block unless that is unavoidable.
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

    // Rule (1): among every option that would actually capture an opponent, prefer the one
    // capturing the opponent piece closest to ITS OWN Home - the opponent piece with the
    // least distance left to travel, i.e. the most advanced and most costly one to send back.
    private static Optional<Command> findBestCapture(List<Command> legalOptions, StrategyContext context) {
        return legalOptions.stream()
                .flatMap(option -> findCapture(option, context).stream())
                .min(Comparator.comparingInt(capture -> distanceToOwnHome(capture.capturedPiece, context.getBoard())))
                .map(capture -> capture.option);
    }

    // T-8: delegates to the shared, capture-rule-aware finder used by every color's strategy.
    private static Optional<Capture> findCapture(Command option, StrategyContext context) {
        return CaptureOpportunityFinder.findCapturedOpponent(option, context)
                .map(capturedPiece -> new Capture(option, capturedPiece));
    }

    private static int distanceToOwnHome(Piece opponentPiece, Board board) {
        return board.getForwardDistance(
                opponentPiece.getTrackPosition(), board.getApproachCellPosition(opponentPiece.getColor()));
    }

    // Rule (2): Red only brings a new piece out of Base once no capture was available above.
    private static Optional<Command> findEnterBoardOption(List<Command> legalOptions) {
        return legalOptions.stream()
                .filter(option -> option.getType() == CommandType.ENTER_BOARD)
                .findFirst();
    }

    // Rule (3): prefer a move that does not land on another Red piece and form a new block.
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

    // Pairs a candidate move with the specific opponent piece it would capture.
    private static final class Capture {
        private final Command option;
        private final Piece capturedPiece;

        private Capture(Command option, Piece capturedPiece) {
            this.option = option;
            this.capturedPiece = capturedPiece;
        }
    }
}
