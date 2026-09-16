package strategy;

import java.util.List;

import command.Command;

// Strategy: decides which legal Command to run when a
// roll allows more than one.
public interface PlayerStrategy {

    Command choose(List<Command> legalOptions, StrategyContext context);
}
