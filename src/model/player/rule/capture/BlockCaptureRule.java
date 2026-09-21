package model.player.rule.capture;

import config.constant.BlockadeConstants;
import java.util.List;
import java.util.Optional;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.command.capture.CaptureBlockCommand;

// T-8: a blockade captures an equal-sized opponent blockade.
public final class BlockCaptureRule extends CaptureCheckRule {

    @Override
    protected Optional<Command> identify(
            Player mover, Piece movedPiece, List<Player> allPlayers) {
        if (!movedPiece.isOnTrack()) {
            return Optional.empty();
        }

        List<Piece> capturingBlock = mover.getPiecesAt(movedPiece.getTrackPosition());

        if (capturingBlock.size() < BlockadeConstants.MINIMUM_BLOCKADE_SIZE) {
            return Optional.empty();
        }

        for (Player opponent : allPlayers) {
            if (opponent.getColor() == mover.getColor()) {
                continue;
            }

            List<Piece> opponentBlock = opponent.getPiecesAt(movedPiece.getTrackPosition());

            if (opponentBlock.size() == capturingBlock.size()) {
                return Optional.of(
                    new CaptureBlockCommand(mover, capturingBlock, opponent, opponentBlock));
            }
        }

        return Optional.empty();
    }
}
