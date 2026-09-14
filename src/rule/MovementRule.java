package rule;

import command.Command;
import command.MoveCommand;
import java.util.Optional;
import ludoboard.Board;
import player.Piece;
import player.Player;

// Rule 1: a piece already on the shared track moves forward by the dice's face value.
public final class MovementRule extends TurnRule {

    @Override
    protected Optional<Command> resolve(Player player, int rollValue, Board board) {
        return findMovablePiece(player)
            .map(piece -> new MoveCommand(player, piece, rollValue, board));
    }

    private static Optional<Piece> findMovablePiece(Player player) {
        return player.getPieces().stream()
            .filter(piece -> !piece.isAtBase())
            .findFirst();
    }
}
