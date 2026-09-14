package rule;

import command.Command;
import java.util.Optional;
import ludoboard.Board;
import player.Player;

// Chain of Responsibility: each rule decides whether it applies to this roll; if not, it defers to the next rule in the chain.
// handle() is the fixed chain-walking template; resolve() is the one thing each concrete rule implements.
public abstract class TurnRule {

    private TurnRule nextRule;

    public final TurnRule setNext(TurnRule nextRule) {
        this.nextRule = nextRule;
        return nextRule;
    }

    public final Optional<Command> handle(Player player, int rollValue, Board board) {
        Optional<Command> command = resolve(player, rollValue, board);
        if (command.isPresent()) {
            return command;
        }
        return nextRule == null
                ? Optional.empty()
                : nextRule.handle(player, rollValue, board);
    }

    protected abstract Optional<Command> resolve(Player player, int rollValue, Board board);
}
