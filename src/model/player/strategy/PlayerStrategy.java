package model.player.strategy;

import java.util.List;
import model.player.command.MoveCommand;

// Strategy: decides which legal Command to run when a roll allows more than one.
public interface PlayerStrategy {

    MoveCommand choose(List<MoveCommand> legalCommands, StrategyContext context);
}
