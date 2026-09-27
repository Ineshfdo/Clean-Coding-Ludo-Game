package model.player.rule.turn;

import java.util.List;
import model.board.Board;
import model.player.Player;
import model.player.command.MoveCommand;

/**
 * A rule that reports every legal command for a roll. The engine collects the commands of all turn
 * rules, and the strategy of the player chooses one of them.
 */
public interface TurnRule {

    /**
     * Finds the commands that this rule allows for the roll.
     *
     * @param player the player whose turn it is
     * @param rollValue the value of the roll
     * @param board the board the pieces move on
     * @param allPlayers all players of the game
     * @return the legal commands; empty when this rule offers nothing
     */
    List<MoveCommand> findLegalCommands(Player player, int rollValue, Board board, List<Player> allPlayers);
}
