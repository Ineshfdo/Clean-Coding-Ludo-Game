package model.player.rule.block;

import java.util.List;
import java.util.Optional;
import model.board.Board;
import model.player.Player;
import model.player.command.Command;
import model.rule.ChainedRule;

// Chain of Responsibility: each rule may force a consequence for this roll.
public abstract class BlockadeBreakRule extends ChainedRule<BlockadeBreakRule> {

    public final Optional<Command> findForcedBreak(
            Player player, int consecutiveSixCount, int rollValue, Board board, List<Player> allPlayers) {
        Optional<Command> command = identify(player, consecutiveSixCount, rollValue, board, allPlayers);

        if (command.isPresent()) {
            return command;
        }

        return getNextRule().flatMap(nextRule ->
                nextRule.findForcedBreak(player, consecutiveSixCount, rollValue, board, allPlayers));
    }

    protected abstract Optional<Command> identify(
            Player player, int consecutiveSixCount, int rollValue, Board board, List<Player> allPlayers);
}
