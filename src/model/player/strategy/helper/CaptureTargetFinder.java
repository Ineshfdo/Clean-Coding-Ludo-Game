package model.player.strategy.helper;

import config.constant.BlockadeConstants;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.strategy.StrategyContext;

// T-16/T-18: finds the opponent piece a candidate move would capture (T-8: blocks need equal size).
public final class CaptureTargetFinder {

    private CaptureTargetFinder() {
    }

    public static Optional<Piece> findTarget(Command option, StrategyContext context) {
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

            boolean capturesHere = moverBlockSize < BlockadeConstants.MINIMUM_BLOCKADE_SIZE
                    || opponentPiecesHere.size() == moverBlockSize;

            if (capturesHere) {
                return Optional.of(opponentPiecesHere.get(0));
            }
        }

        return Optional.empty();
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
}
