package model.player.command;

import config.enums.CommandType;
import java.util.List;
import java.util.Optional;
import message.observer.GameMessagePublisher;
import model.piece.Piece;

// One action wrapped as an object, so callers run it without knowing how.
public interface Command {

    void execute(GameMessagePublisher messages);

    CommandType getType();

    Piece getAffectedPiece();

    // T-6: most commands affect one piece; a forced breakup can affect several.
    default List<Piece> getAffectedPieces() {
        return List.of(getAffectedPiece());
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
}
