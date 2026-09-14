package rule;

import java.util.List;
import java.util.Optional;

import command.Command;
import player.Piece;
import player.Player;

// Chain of Responsibility: each rule checks whether the piece that
// just moved captures an opponent, else defers onward.
public abstract class CaptureRule {

    private CaptureRule nextRule;

    public final CaptureRule setNext(CaptureRule nextRule) {
        this.nextRule = nextRule;
        return nextRule;
    }

    public final Optional<Command> resolve(Player mover, Piece movedPiece, List<Player> allPlayers) {
        Optional<Command> command = identify(mover, movedPiece, allPlayers);
        if (command.isPresent()) {
            return command;
        }
        return nextRule == null
                ? Optional.empty()
                : nextRule.resolve(mover, movedPiece, allPlayers);
    }

    protected abstract Optional<Command> identify(
            Player mover, Piece movedPiece, List<Player> allPlayers);
}
