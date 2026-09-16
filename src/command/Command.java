package command;

import java.util.List;
import java.util.Optional;

import gamemessage.GameMessagePublisher;
import player.Piece;

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
}
