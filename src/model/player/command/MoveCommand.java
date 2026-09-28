package model.player.command;

import java.util.Optional;

/**
 A command that a player strategy may choose for the current roll.
 Besides running, it can answer questions, so strategies can compare commands without executing them.
 Every answer is no by default, and each command answers yes only to what it really is.
 */
public interface MoveCommand extends Command {

    /**
     Tells whether the command brings a piece out of Base.
     @return true for entering the board
     */
    default boolean entersBoard() {
        return false;
    }

    /**
     Tells whether the command only announces that nothing can move.
     @return true for a cannot-move command
     */
    default boolean movesNothing() {
        return false;
    }

    /**
     Shows the track cell where the moved piece would land, without moving it (T-16).
     @return the track position, or an empty result when there is no plain track landing
     */
    default Optional<Integer> previewLandingPosition() {
        return Optional.empty();
    }

    /**
     Tells whether the move would send the piece Home (T-17).
     @return true when the piece reaches Home
     */
    default boolean reachesHome() {
        return false;
    }

    /**
     Tells whether the move keeps an existing blockade of two or more pieces together (T-17).
     @return true for a blockade move
     */
    default boolean movesExistingBlock() {
        return false;
    }

    /**
     Tells whether the move breaks a piece away from its blockade (T-17).
     @return true when the blockade is broken
     */
    default boolean breaksExistingBlock() {
        return false;
    }

    /**
     Tells whether the move carries a track piece off the shared track, into its HomeStraight or Home.
     @return true when the piece leaves the track
     */
    default boolean leavesStandardPath() {
        return false;
    }
}
