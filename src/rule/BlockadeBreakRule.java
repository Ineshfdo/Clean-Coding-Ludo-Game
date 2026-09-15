package rule;

import command.Command;
import java.util.List;
import java.util.Optional;
import ludoboard.Board;
import player.Player;

// Chain of Responsibility: each rule may force a consequence for this roll, else defers.
public abstract class BlockadeBreakRule {

    private BlockadeBreakRule nextRule;

    public final BlockadeBreakRule setNext(BlockadeBreakRule nextRule) {
        this.nextRule = nextRule;
        return nextRule;
    }

    public final Optional<Command> resolve(
            Player player, int rollNumber, int rollValue, Board board, List<Player> allPlayers) {
        Optional<Command> command = identify(player, rollNumber, rollValue, board, allPlayers);
        if (command.isPresent()) {
            return command;
        }
        return nextRule == null
            ? Optional.empty()
            : nextRule.resolve(player, rollNumber, rollValue, board, allPlayers);
    }

    protected abstract Optional<Command> identify(
        Player player, int rollNumber, int rollValue, Board board, List<Player> allPlayers);
}
