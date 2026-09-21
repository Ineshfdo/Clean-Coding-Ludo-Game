package model.player.rule.mystery;

import config.constant.TurnConstants;
import java.util.List;
import java.util.Optional;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.command.mystery.BetaReturnToBaseCommand;

// T-13: consecutive 3s on the first roll send a Beta piece back to Base.
public final class BetaRestrictionRule {

    public void recordRoll(Player player, int rollNumber, int rollValue) {
        if (rollNumber != TurnConstants.FIRST_ROLL_OF_TURN) {
            return;
        }

        player.recordRestrictionRoll(rollValue);
    }

    public Optional<Command> findReturnToBase(Player player) {
        List<Piece> triggeredPieces = player.findPiecesTriggeredForReturnToBase();

        if (triggeredPieces.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new BetaReturnToBaseCommand(player, triggeredPieces));
    }
}
