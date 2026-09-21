package model.player.command;

import java.util.Optional;

// A command a player strategy may choose for this roll, plus what strategies need to compare them.
public interface MoveCommand extends Command {

    // Does this command bring a piece out of Base?
    default boolean entersBoard() {
        return false;
    }

    // Is this only an announcement that nothing can move?
    default boolean movesNothing() {
        return false;
    }

    // T-16: previews the track landing cell before executing (empty by default).
    default Optional<Integer> previewLandingPosition() {
        return Optional.empty();
    }

    // T-17: would this move send the piece Home?
    default boolean reachesHome() {
        return false;
    }

    // T-17: does this move keep an existing block (2+ pieces) together?
    default boolean movesExistingBlock() {
        return false;
    }

    // T-17: does this move break a piece away from its block?
    default boolean breaksExistingBlock() {
        return false;
    }

    // Red: would this move carry a track piece off the standard path (into HomeStraight or Home)?
    default boolean leavesStandardPath() {
        return false;
    }
}
