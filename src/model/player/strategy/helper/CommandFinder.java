package model.player.strategy.helper;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import model.player.command.MoveCommand;

// Picks the first command that satisfies a strategy's condition.
public final class CommandFinder {

    private CommandFinder() {
    }

    public static Optional<MoveCommand> findFirst(List<MoveCommand> commands, Predicate<MoveCommand> condition) {
        return commands.stream().filter(condition).findFirst();
    }
}
