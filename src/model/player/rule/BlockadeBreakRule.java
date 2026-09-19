package model.player.rule;

import model.player.action.Command;
import java.util.List;
import java.util.Optional;
import model.board.Board;
import model.player.Player;

// Chain of Responsibility: each rule may force a consequence for this roll, else defers.
public abstract class BlockadeBreakRule {

    private BlockadeBreakRule nextRule;

    public final BlockadeBreakRule setNext(BlockadeBreakRule nextRule) {
        this.nextRule = nextRule;
        return nextRule;
    }

    public final Optional<Command> resolve(
            Player player, int consecutiveSixCount, int rollValue, Board board, List<Player> allPlayers) {
        Optional<Command> command = identify(player, consecutiveSixCount, rollValue, board, allPlayers);
        if (command.isPresent()) {
            return command;
        }
        return nextRule == null
            ? Optional.empty()
            : nextRule.resolve(player, consecutiveSixCount, rollValue, board, allPlayers);
    }

    protected abstract Optional<Command> identify(
        Player player, int consecutiveSixCount, int rollValue, Board board, List<Player> allPlayers);
}
