package rule;

import command.CaptureCommand;
import command.Command;
import java.util.List;
import java.util.Optional;
import player.Piece;
import player.Player;

// Rule 7: a lone piece landing on an opponent's track cell captures it.
// T-8: a blockade mover defers entirely to BlockCaptureRule - it never captures a non-equal-size target.
public final class OpponentCaptureRule extends CaptureRule {

    private static final int BLOCKADE_PIECE_COUNT = 2;

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
                        new CaptureCommand(mover, movedPiece, opponent, capturedPiece.get()));
            }
        }

        return Optional.empty();
    }

    // T-8: a blockade of 2+ own pieces here can only capture via BlockCaptureRule
    private static boolean isMovingAsBlockade(Player mover, Piece movedPiece) {
        return countOwnPiecesAt(mover, movedPiece.getTrackPosition()) >= BLOCKADE_PIECE_COUNT;
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
