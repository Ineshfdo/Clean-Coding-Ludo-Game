package rule;

import java.util.List;
import java.util.Optional;

import command.CaptureCommand;
import command.Command;
import player.Piece;
import player.Player;

// Rule 7: landing on a shared track cell occupied by an opponent's
// piece captures it. HomeStraight/Home cells are single-color, so
// only track landings can ever trigger this.
public final class OpponentCaptureRule extends CaptureRule {

    @Override
    protected Optional<Command> identify(
            Player mover, Piece movedPiece, List<Player> allPlayers) {
        if (!movedPiece.isOnTrack()) {
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

    private static Optional<Piece> findPieceAt(Player player, int trackPosition) {
        return player.getPieces().stream()
                .filter(Piece::isOnTrack)
                .filter(piece -> piece.getTrackPosition() == trackPosition)
                .findFirst();
    }
}
