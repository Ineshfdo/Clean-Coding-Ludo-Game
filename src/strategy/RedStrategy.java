package strategy;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import command.Command;
import command.CommandType;
import ludoboard.Board;
import ludoboard.PlayerColor;
import player.Piece;
import player.Player;

// T-16: Red is an aggressive, capture-focused player. It prioritizes capturing an opponent
// piece over any other move - preferring, among several available captures, the opponent
// closest to Red's own Home - only brings a new piece out of Base once no capture is
// available, and otherwise avoids landing on its own pieces to form a new block unless that
// is unavoidable.
public final class RedStrategy implements PlayerStrategy {

    private static final int BLOCKADE_PIECE_COUNT = 2;

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
    // capturing the opponent piece closest to Red's own Home.
    private Optional<Command> findBestCapture(List<Command> legalOptions, StrategyContext context) {
        return legalOptions.stream()
                .flatMap(option -> findCapture(option, context).stream())
                .min(Comparator.comparingInt(capture -> distanceToRedHome(capture.capturedPiece, context.getBoard())))
                .map(capture -> capture.option);
    }

    // T-8: a lone mover captures any opponent it can legally reach; a moving block only
    // captures an opponent block of the exact same size - anything else is not a real capture.
    private Optional<Capture> findCapture(Command option, StrategyContext context) {
        Optional<Integer> landingPosition = option.previewLandingPosition();
        if (landingPosition.isEmpty()) {
            return Optional.empty();
        }

        int moverBlockSize = countOwnPiecesAt(context.getPlayer(), option.getAffectedPiece().getTrackPosition());
        for (Player opponent : context.getAllPlayers()) {
            if (opponent.getColor() == context.getPlayer().getColor()) {
                continue;
            }
            List<Piece> opponentPiecesHere = findPiecesAt(opponent, landingPosition.get());
            if (opponentPiecesHere.isEmpty()) {
                continue;
            }
            boolean capturesHere = moverBlockSize < BLOCKADE_PIECE_COUNT
                    || opponentPiecesHere.size() == moverBlockSize;
            if (capturesHere) {
                return Optional.of(new Capture(option, opponentPiecesHere.get(0)));
            }
        }
        return Optional.empty();
    }

    private static int distanceToRedHome(Piece opponentPiece, Board board) {
        return board.getForwardDistance(
                opponentPiece.getTrackPosition(), board.getApproachCellPosition(PlayerColor.RED));
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

    private static List<Piece> findPiecesAt(Player player, int trackPosition) {
        return player.getPieces().stream()
                .filter(Piece::isOnTrack)
                .filter(piece -> piece.getTrackPosition() == trackPosition)
                .collect(Collectors.toList());
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
