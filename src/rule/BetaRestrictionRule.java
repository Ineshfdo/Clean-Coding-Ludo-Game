package rule;

import java.util.List;
import java.util.Optional;

import command.Command;
import command.ReturnToBaseCommand;
import player.Piece;
import player.Player;

// T-13: while a piece/block sits Beta-restricted, its owner's first roll each round is checked
// for two consecutive rounds of a 3 - the condition that forces it back to Base.
public final class BetaRestrictionRule {

    private static final int FIRST_ROLL_OF_TURN = 1;

    public Optional<Command> resolve(Player player, int rollNumber, int rollValue) {
        if (rollNumber != FIRST_ROLL_OF_TURN) {
            return Optional.empty();
        }

        player.recordRestrictionRoll(rollValue);
        List<Piece> triggeredPieces = player.findPiecesTriggeredForReturnToBase();
        if (triggeredPieces.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new ReturnToBaseCommand(player, triggeredPieces));
    }
}
