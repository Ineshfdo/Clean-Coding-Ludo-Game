package model.player.strategy.helper;

import config.constant.BlockadeConstants;
import java.util.List;
import java.util.Optional;
import model.piece.Piece;
import model.player.Player;
import model.player.command.MoveCommand;
import model.player.strategy.StrategyContext;

/**
 Finds the opponent piece that a candidate move would capture (T-16, T-18).
 A blockade can only capture an opponent blockade of the same size (T-8).
 */
public final class CaptureTargetFinder {

    private CaptureTargetFinder() {
    }

    /**
     Previews the move and looks for a capture.
     @param command the candidate move
     @param context the situation of the player
     @return the piece that would be captured, or an empty result
     */
    public static Optional<Piece> findTarget(MoveCommand command, StrategyContext context) {
        Optional<Integer> previewedLandingPosition = command.previewLandingPosition();

        if (previewedLandingPosition.isEmpty()) {
            return Optional.empty();
        }

        int moverBlockSize =
                context.getPlayer().getPiecesAt(command.getAffectedPiece().getTrackPosition()).size();

        for (Player opponent : context.getAllPlayers()) {
            if (opponent.getColor() == context.getPlayer().getColor()) {
                continue;
            }

            List<Piece> opponentPiecesHere = opponent.getPiecesAt(previewedLandingPosition.get());

            if (opponentPiecesHere.isEmpty()) {
                continue;
            }

            boolean capturesHere = moverBlockSize < BlockadeConstants.MINIMUM_BLOCKADE_SIZE
                    || opponentPiecesHere.size() == moverBlockSize;

            if (capturesHere) {
                return Optional.of(opponentPiecesHere.get(0));
            }
        }

        return Optional.empty();
    }
}
