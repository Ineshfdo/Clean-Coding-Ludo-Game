package rule;

import java.util.Optional;

import command.Command;
import ludoboard.Board;
import player.Player;

// Each rule decides if it applies to this roll;
// GameFacade collects every answer.
public interface TurnRule {

    Optional<Command> resolve(Player player, int rollValue, Board board);
}
