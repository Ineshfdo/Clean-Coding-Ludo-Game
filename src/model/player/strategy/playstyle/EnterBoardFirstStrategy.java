package model.player.strategy.playstyle;

import java.util.List;
import model.player.command.MoveCommand;
import model.player.strategy.PlayerStrategy;
import model.player.strategy.StrategyContext;

/**
 The default strategy.
 It prefers to bring a new piece onto the board, and otherwise takes the first legal command.
 */
public final class EnterBoardFirstStrategy implements PlayerStrategy {

    @Override
    public MoveCommand choose(List<MoveCommand> legalCommands, StrategyContext context) {
        return legalCommands.stream()
            .filter(command -> command.entersBoard())
            .findFirst()
            .orElse(legalCommands.get(0));
    }
}
