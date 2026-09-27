package model.player.rule.capture;

import config.constant.BlockadeConstants;
import java.util.List;
import java.util.Optional;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.command.capture.CapturePieceCommand;

/**
 * A single piece that lands on a single opponent piece captures it (rule 7). A single piece cannot
 * capture a blockade (T-8).
 */
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

            Optional<Piece> capturedPiece = opponent.getPiecesAt(trackPosition).stream().findFirst();

            if (capturedPiece.isPresent() && !hasBlockadeAt(opponent, trackPosition)) {
                return Optional.of(
                    new CapturePieceCommand(mover, movedPiece, opponent, capturedPiece.get()));
            }
        }

        return Optional.empty();
    }

    // T-3/T-8: 2+ pieces of one colour on a cell form a blockade.
    private static boolean hasBlockadeAt(Player player, int trackPosition) {
        return player.getPiecesAt(trackPosition).size() >= BlockadeConstants.MINIMUM_BLOCKADE_SIZE;
    }
}
