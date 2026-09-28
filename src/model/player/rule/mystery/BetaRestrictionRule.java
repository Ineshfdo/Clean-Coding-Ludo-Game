package model.player.rule.mystery;

import config.constant.TurnConstants;
import java.util.List;
import java.util.Optional;
import message.observer.GameMessagePublisher;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.command.mystery.BetaReturnToBaseCommand;
import model.player.rule.roll.RollEvent;
import model.player.rule.roll.RollHook;

/**
 Roll hook for the Beta restriction (T-13).
 The first roll of every turn is counted for the restricted pieces, and two rolls of 3 in a row send them back to Base.
 */
public final class BetaRestrictionRule implements RollHook {

    // T-13: forces a still-restricted Beta piece back to Base.
    @Override
    public void onRollAccepted(RollEvent roll, GameMessagePublisher messagePublisher) {
        recordRoll(roll.getPlayer(), roll.getRollNumber(), roll.getRollValue());
        findReturnToBase(roll.getPlayer()).ifPresent(command -> command.execute(messagePublisher));
    }

    private void recordRoll(Player player, int rollNumber, int rollValue) {
        if (rollNumber != TurnConstants.FIRST_ROLL_OF_TURN) {
            return;
        }

        player.recordRestrictionRoll(rollValue);
    }

    private Optional<Command> findReturnToBase(Player player) {
        List<Piece> triggeredPieces = player.findPiecesTriggeredForReturnToBase();

        if (triggeredPieces.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new BetaReturnToBaseCommand(player, triggeredPieces));
    }
}
