package model.player.strategy.playstyle;

import java.util.List;
import model.player.command.MoveCommand;
import model.player.strategy.PlayerStrategy;
import model.player.strategy.StrategyContext;

// Default strategy: prefers bringing a new piece onto the board.
public final class EnterBoardFirstStrategy implements PlayerStrategy {

    @Override
    public MoveCommand choose(List<MoveCommand> legalCommands, StrategyContext context) {
        return legalCommands.stream()
            .filter(command -> command.entersBoard())
            .findFirst()
            .orElse(legalCommands.get(0));
    }
}
