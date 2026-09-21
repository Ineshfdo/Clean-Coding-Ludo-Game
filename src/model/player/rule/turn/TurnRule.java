package model.player.rule.turn;

import java.util.List;
import model.board.Board;
import model.player.Player;
import model.player.command.Command;

// Each rule reports every legal command for this roll.
public interface TurnRule {

    List<Command> findLegalCommands(Player player, int rollValue, Board board, List<Player> allPlayers);
}
