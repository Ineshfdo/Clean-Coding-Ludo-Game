package command;

import java.util.List;

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
}
