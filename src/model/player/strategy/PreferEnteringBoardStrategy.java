package model.player.strategy;

import model.player.command.Command;
import config.enums.CommandType;
import java.util.List;

// Default strategy: prefer bringing a new piece onto the
// board over moving one in play.
public final class PreferEnteringBoardStrategy implements PlayerStrategy {

    @Override
    public Command choose(List<Command> legalOptions, StrategyContext context) {
        return legalOptions.stream()
            .filter(option -> option.getType() == CommandType.ENTER_BOARD)
            .findFirst()
            .orElse(legalOptions.get(0));
    }
}
