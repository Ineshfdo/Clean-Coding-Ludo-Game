package model.player.rule.block;

import java.util.List;
import java.util.Optional;
import model.board.Board;
import model.player.Player;
import model.player.command.Command;
import model.rule.ChainedRule;

/**
 Chain of Responsibility where each rule may force a consequence for the roll, for example breaking a blockade on a third six (T-6).
 The first rule that has an answer wins.
 */
public abstract class BlockadeBreakRule extends ChainedRule<BlockadeBreakRule> {

    /**
     Asks this rule and then the next rules of the chain.
     @param player the player whose turn it is
     @param consecutiveSixCount the number of sixes in a row, including this roll
     @param rollValue the value of the roll
     @param board the board the pieces move on
     @param allPlayers all players of the game
     @return the command to run instead of a normal move, or an empty result
     */
    public final Optional<Command> findForcedBreak(
            Player player, int consecutiveSixCount, int rollValue, Board board, List<Player> allPlayers) {
        Optional<Command> command = identify(player, consecutiveSixCount, rollValue, board, allPlayers);

        if (command.isPresent()) {
            return command;
        }

        return getNextRule().flatMap(nextRule ->
                nextRule.findForcedBreak(player, consecutiveSixCount, rollValue, board, allPlayers));
    }

    /**
     Checks the condition of this rule alone.
     @param player the player whose turn it is
     @param consecutiveSixCount the number of sixes in a row, including this roll
     @param rollValue the value of the roll
     @param board the board the pieces move on
     @param allPlayers all players of the game
     @return the forced command, or an empty result
     */
    protected abstract Optional<Command> identify(
            Player player, int consecutiveSixCount, int rollValue, Board board, List<Player> allPlayers);
}
