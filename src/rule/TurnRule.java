package rule;

import java.util.List;

import command.Command;
import ludoboard.Board;
import player.Player;

// Each rule reports every option it finds legal for this roll;
// GameFacade collects every answer.
public interface TurnRule {

    List<Command> resolve(Player player, int rollValue, Board board, List<Player> allPlayers);
}
