package model.player.strategy;

import java.util.List;
import model.player.command.Command;

// Strategy: decides which legal Command to run when a roll allows more than one.
public interface PlayerStrategy {

    Command choose(List<Command> legalCommands, StrategyContext context);
}
