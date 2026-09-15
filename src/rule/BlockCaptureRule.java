package rule;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import command.CaptureBlockCommand;
import command.Command;
import player.Piece;
import player.Player;

// T-8: an equal-sized blockade captures an opponent's blockade of the same size.
public final class BlockCaptureRule extends CaptureRule {

    private static final int BLOCKADE_PIECE_COUNT = 2;

    @Override
    protected Optional<Command> identify(
            Player mover, Piece movedPiece, List<Player> allPlayers) {
        if (!movedPiece.isOnTrack()) {
            return Optional.empty();
        }

        List<Piece> capturingBlock = findBlockAt(mover, movedPiece.getTrackPosition());
        if (capturingBlock.size() < BLOCKADE_PIECE_COUNT) {
            return Optional.empty();
        }

        for (Player opponent : allPlayers) {
            if (opponent.getColor() == mover.getColor()) {
                continue;
            }

            List<Piece> opponentBlock = findBlockAt(opponent, movedPiece.getTrackPosition());
            if (opponentBlock.size() == capturingBlock.size()) {
                return Optional.of(
                        new CaptureBlockCommand(mover, capturingBlock, opponent, opponentBlock));
            }
        }

        return Optional.empty();
    }

    private static List<Piece> findBlockAt(Player player, int trackPosition) {
        return player.getPieces().stream()
                .filter(Piece::isOnTrack)
                .filter(piece -> piece.getTrackPosition() == trackPosition)
                .collect(Collectors.toList());
    }
}
