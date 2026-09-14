package rule;

import java.util.Optional;

import command.Command;
import ludoboard.Board;
import player.Player;

// Each rule independently decides whether it applies to this roll.
// GameFacade asks every rule and collects every answer, since a
// single roll (a 6) can legally satisfy more than one rule at once -
// PlayerStrategy chooses among whatever comes back.
public interface TurnRule {

    Optional<Command> resolve(Player player, int rollValue, Board board);
}
