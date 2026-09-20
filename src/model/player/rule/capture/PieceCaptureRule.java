package model.player.rule.capture;

import config.constant.BlockadeConstants;
import java.util.List;
import java.util.Optional;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.command.capture.CapturePieceCommand;

// Rule 7: a lone piece landing on an opponent captures it.
// T-8: a blockade mover defers to BlockCaptureRule; a lone piece can't capture a blockade.
public final class PieceCaptureRule extends CaptureCheckRule {

    @Override
    protected Optional<Command> identify(
            Player mover, Piece movedPiece, List<Player> allPlayers) {
        if (!movedPiece.isOnTrack() || hasBlockadeAt(mover, movedPiece.getTrackPosition())) {
            return Optional.empty();
        }

        int trackPosition = movedPiece.getTrackPosition();

        for (Player opponent : allPlayers) {
            if (opponent.getColor() == mover.getColor()) {
                continue;
            }

            Optional<Piece> capturedPiece = findPieceAt(opponent, trackPosition);

            if (capturedPiece.isPresent() && !hasBlockadeAt(opponent, trackPosition)) {
                return Optional.of(
                    new CapturePieceCommand(mover, movedPiece, opponent, capturedPiece.get()));
            }
        }

        return Optional.empty();
    }

    // T-3/T-8: 2+ pieces of one colour on a cell form a blockade.
    private static boolean hasBlockadeAt(Player player, int trackPosition) {
        return countPiecesAt(player, trackPosition) >= BlockadeConstants.MINIMUM_BLOCKADE_SIZE;
    }

    private static long countPiecesAt(Player player, int trackPosition) {
        return player.getPieces().stream()
            .filter(Piece::isOnTrack)
            .filter(piece -> piece.getTrackPosition() == trackPosition)
            .count();
    }

    private static Optional<Piece> findPieceAt(Player player, int trackPosition) {
        return player.getPieces().stream()
            .filter(Piece::isOnTrack)
            .filter(piece -> piece.getTrackPosition() == trackPosition)
            .findFirst();
    }
}
