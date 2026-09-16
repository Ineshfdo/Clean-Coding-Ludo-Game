package strategy;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import command.Command;
import player.Piece;
import player.Player;

// T-16/T-18: shared by RedStrategy and YellowStrategy - determines whether a candidate move
// would genuinely capture an opponent, respecting T-8 (a moving block only captures an
// opponent block of the exact same size; a lone mover captures anything it can legally reach).
public final class CaptureOpportunityFinder {

    private static final int BLOCKADE_PIECE_COUNT = 2;

    private CaptureOpportunityFinder() {
    }

    public static Optional<Piece> findCapturedOpponent(Command option, StrategyContext context) {
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
