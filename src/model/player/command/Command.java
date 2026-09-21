package model.player.command;

import java.util.List;
import message.observer.GameMessagePublisher;
import model.piece.Piece;

// One action wrapped as an object, so callers run it without knowing how.
public interface Command {

    void execute(GameMessagePublisher messagePublisher);

    // The pieces whose state this command changes.
    Piece getAffectedPiece();

    // T-6: most commands affect one piece; a forced breakup can affect several.
    default List<Piece> getAffectedPieces() {
        return List.of(getAffectedPiece());
    }
}
