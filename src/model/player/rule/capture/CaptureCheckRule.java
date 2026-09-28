package model.player.rule.capture;

import java.util.List;
import java.util.Optional;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.rule.ChainedRule;

/**
 Chain of Responsibility where each rule checks whether the moved piece captures something (rule 7, T-8).
 The first rule that finds a capture wins.
 */
public abstract class CaptureCheckRule extends ChainedRule<CaptureCheckRule> {

    /**
     Asks this rule and then the next rules of the chain.
     @param mover the player who moved
     @param movedPiece the piece that has just moved
     @param allPlayers all players of the game
     @return the capture command, or an empty result when nothing is captured
     */
    public final Optional<Command> findCapture(Player mover, Piece movedPiece, List<Player> allPlayers) {
        Optional<Command> command = identify(mover, movedPiece, allPlayers);

        if (command.isPresent()) {
            return command;
        }

        return getNextRule().flatMap(nextRule -> nextRule.findCapture(mover, movedPiece, allPlayers));
    }

    /**
     Checks the condition of this rule alone.
     @param mover the player who moved
     @param movedPiece the piece that has just moved
     @param allPlayers all players of the game
     @return the capture command, or an empty result
     */
    protected abstract Optional<Command> identify(
            Player mover, Piece movedPiece, List<Player> allPlayers);
}
