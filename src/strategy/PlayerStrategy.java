package strategy;

import java.util.List;

import command.Command;

// Strategy: decides which legal Command to run when a roll makes
// more than one action available - e.g. rolling a 6 while a piece
// could either enter the board or move six cells.
public interface PlayerStrategy {

    Command choose(List<Command> legalOptions);
}
