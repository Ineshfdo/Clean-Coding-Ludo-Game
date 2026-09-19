package model.player.rule;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import config.constant.BlockadeConstants;
import model.player.command.capture.CaptureBlockCommand;
import model.player.command.Command;
import model.piece.Piece;
import model.player.Player;

// T-8: an equal-sized blockade captures an opponent's blockade of the same size.
public final class BlockCaptureRule extends CaptureRule {

    @Override
    protected Optional<Command> identify(
            Player mover, Piece movedPiece, List<Player> allPlayers) {
        if (!movedPiece.isOnTrack()) {
            return Optional.empty();
        }

        List<Piece> capturingBlock = findBlockAt(mover, movedPiece.getTrackPosition());
        if (capturingBlock.size() < BlockadeConstants.MINIMUM_BLOCKADE_SIZE) {
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
