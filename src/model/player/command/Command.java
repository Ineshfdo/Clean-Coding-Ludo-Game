package model.player.command;

import java.util.List;
import message.observer.GameMessagePublisher;
import model.piece.Piece;

/**
 Command pattern: one action wrapped as an object, so that callers can run it without knowing how it works.
 Moves, captures, teleports and returns to Base are all commands.
 */
public interface Command {

    /**
     Carries out the action and announces what happened.
     @param messagePublisher where the events are announced
     */
    void execute(GameMessagePublisher messagePublisher);

    /**
     Gives the main piece whose state this command changes.
     @return the affected piece
     */
    Piece getAffectedPiece();

    /**
     Gives every piece whose state this command changes (T-6).
     Most commands affect one piece, but a blockade command lists all its members.
     @return the affected pieces
     */
    default List<Piece> getAffectedPieces() {
        return List.of(getAffectedPiece());
    }
}
