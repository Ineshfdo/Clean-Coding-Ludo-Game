package model.player.strategy.playstyle;

import config.enums.CommandType;
import java.util.List;
import model.player.command.Command;
import model.player.strategy.PlayerStrategy;
import model.player.strategy.StrategyContext;

// Default strategy: prefers bringing a new piece onto the board.
public final class EnterBoardFirstStrategy implements PlayerStrategy {

    @Override
    public Command choose(List<Command> legalCommands, StrategyContext context) {
        return legalCommands.stream()
            .filter(command -> command.getType() == CommandType.ENTER_BOARD)
            .findFirst()
            .orElse(legalCommands.get(0));
    }
}
