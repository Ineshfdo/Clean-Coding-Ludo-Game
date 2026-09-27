package model.player.strategy;

import java.util.List;
import model.player.command.MoveCommand;

/**
 * Strategy pattern: decides which legal command to run when a roll allows more than one. Each
 * colour has its own implementation, which is the rule-based AI of that player.
 */
public interface PlayerStrategy {

    /**
     * Chooses one command.
     *
     * @param legalCommands all legal commands for this roll; the list is not empty
     * @param context the situation of the player
     * @return the chosen command, which is one of the legal commands
     */
    MoveCommand choose(List<MoveCommand> legalCommands, StrategyContext context);
}
