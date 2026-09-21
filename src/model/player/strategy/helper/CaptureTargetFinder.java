package model.player.strategy.helper;

import config.constant.BlockadeConstants;
import java.util.List;
import java.util.Optional;
import model.piece.Piece;
import model.player.Player;
import model.player.command.MoveCommand;
import model.player.strategy.StrategyContext;

// T-16/T-18: finds the opponent piece a candidate move would capture (T-8: blocks need equal size).
public final class CaptureTargetFinder {

    private CaptureTargetFinder() {
    }

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
