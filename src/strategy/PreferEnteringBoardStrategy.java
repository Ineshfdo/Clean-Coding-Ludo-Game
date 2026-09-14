package strategy;

import command.Command;
import command.CommandType;
import java.util.List;

// Default rule-based choice: prefer bringing a new piece onto the
// board over moving one already in play, since getting more pieces
// active early reduces the risk of having no legal move later.
public final class PreferEnteringBoardStrategy implements PlayerStrategy {

    @Override
    public Command choose(List<Command> legalOptions) {
        return legalOptions.stream()
            .filter(option -> option.getType() == CommandType.ENTER_BOARD)
            .findFirst()
            .orElse(legalOptions.get(0));
    }
}
