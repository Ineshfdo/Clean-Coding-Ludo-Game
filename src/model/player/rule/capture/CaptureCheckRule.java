package model.player.rule.capture;

import java.util.List;
import java.util.Optional;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.rule.ChainedRule;

// Chain of Responsibility: each rule checks whether the moved piece captures.
public abstract class CaptureCheckRule extends ChainedRule<CaptureCheckRule> {

    public final Optional<Command> findCapture(Player mover, Piece movedPiece, List<Player> allPlayers) {
        Optional<Command> command = identify(mover, movedPiece, allPlayers);

        if (command.isPresent()) {
            return command;
        }

        return getNextRule().flatMap(nextRule -> nextRule.findCapture(mover, movedPiece, allPlayers));
    }

    protected abstract Optional<Command> identify(
            Player mover, Piece movedPiece, List<Player> allPlayers);
}
