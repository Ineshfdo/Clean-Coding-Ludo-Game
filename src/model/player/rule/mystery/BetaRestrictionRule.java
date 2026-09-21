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

// T-13: consecutive 3s on the first roll send a Beta piece back to Base.
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
