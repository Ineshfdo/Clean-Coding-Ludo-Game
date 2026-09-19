package model.player.command;
import config.enums.CommandType;

import java.util.List;
import java.util.Optional;

import view.observer.GameMessagePublisher;
import model.piece.Piece;

// Wraps one action as an object, so callers run it
// without knowing how it works.
public interface Command {

    void execute(GameMessagePublisher messages);

    CommandType getType();

    Piece getAffectedPiece();

    // T-6: most commands move one piece; a forced breakup overrides this for several.
    default List<Piece> getAffectedPieces() {
        return List.of(getAffectedPiece());
    }

    // T-16: lets a Strategy preview a move's standard-track landing cell before executing it,
    // e.g. to check for a capture. Most commands don't move forward on the track in a way
    // that matters for this, so they simply keep the default.
    default Optional<Integer> previewLandingPosition() {
        return Optional.empty();
    }

    // T-17: would this move send the piece Home? Used by GreenStrategy's Home-first priority.
    default boolean reachesHome() {
        return false;
    }

    // T-17: does this move keep an EXISTING 2+ piece block moving together (T-4)?
    default boolean movesExistingBlock() {
        return false;
    }

    // T-17: does this move represent a piece breaking away from (leaving) its block?
    default boolean breaksExistingBlock() {
        return false;
    }
}
