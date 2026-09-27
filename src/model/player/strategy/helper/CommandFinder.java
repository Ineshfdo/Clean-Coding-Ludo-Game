package model.player.strategy.helper;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import model.player.command.MoveCommand;

/**
 * Picks the first command that satisfies a condition of a strategy.
 */
public final class CommandFinder {

    private CommandFinder() {
    }

    /**
     * Searches the commands in order.
     *
     * @param commands the legal commands
     * @param condition the condition a command must satisfy
     * @return the first matching command, or an empty result
     */
    public static Optional<MoveCommand> findFirst(List<MoveCommand> commands, Predicate<MoveCommand> condition) {
        return commands.stream().filter(condition).findFirst();
    }
}
