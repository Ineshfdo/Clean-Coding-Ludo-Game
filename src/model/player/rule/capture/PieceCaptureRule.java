package model.player.rule.capture;

import config.constant.BlockadeConstants;
import java.util.List;
import java.util.Optional;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.command.capture.CapturePieceCommand;

// Rule 7: a lone piece landing on an opponent captures it.
// T-8: a blockade mover defers to BlockCaptureRule.
public final class PieceCaptureRule extends CaptureCheckRule {

    @Override
    protected Optional<Command> identify(
            Player mover, Piece movedPiece, List<Player> allPlayers) {
        if (!movedPiece.isOnTrack() || isMovingAsBlockade(mover, movedPiece)) {
            return Optional.empty();
        }

        for (Player opponent : allPlayers) {
            if (opponent.getColor() == mover.getColor()) {
                continue;
            }

            Optional<Piece> capturedPiece = findPieceAt(opponent, movedPiece.getTrackPosition());

            if (capturedPiece.isPresent()) {
                return Optional.of(
                    new CapturePieceCommand(mover, movedPiece, opponent, capturedPiece.get()));
            }
        }

        return Optional.empty();
    }

    // T-8: 2+ own pieces here can only capture via BlockCaptureRule.
    private static boolean isMovingAsBlockade(Player mover, Piece movedPiece) {
        return countOwnPiecesAt(mover, movedPiece.getTrackPosition()) >= BlockadeConstants.MINIMUM_BLOCKADE_SIZE;
    }

    private static long countOwnPiecesAt(Player player, int trackPosition) {
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
