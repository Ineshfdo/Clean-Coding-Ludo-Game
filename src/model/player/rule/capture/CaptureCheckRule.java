package model.player.rule.capture;

import java.util.List;
import java.util.Optional;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;

// Chain of Responsibility: each rule checks whether the moved piece captures.
public abstract class CaptureCheckRule {

    private CaptureCheckRule nextRule;

    public final void setNext(CaptureCheckRule nextRule) {
        this.nextRule = nextRule;
    }

    public final Optional<Command> findCapture(Player mover, Piece movedPiece, List<Player> allPlayers) {
        Optional<Command> command = identify(mover, movedPiece, allPlayers);

        if (command.isPresent()) {
            return command;
        }

        return nextRule == null
            ? Optional.empty()
            : nextRule.findCapture(mover, movedPiece, allPlayers);
    }

    protected abstract Optional<Command> identify(
        Player mover, Piece movedPiece, List<Player> allPlayers);
}
