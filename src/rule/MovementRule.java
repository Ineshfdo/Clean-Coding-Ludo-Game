package rule;

import java.util.Optional;

import command.Command;
import command.MoveCommand;
import ludoboard.Board;
import player.Piece;
import player.Player;

// Rule 1: a piece already on the track moves forward
// by the dice's value.
public final class MovementRule implements TurnRule {

    @Override
    public Optional<Command> resolve(Player player, int rollValue, Board board) {
        return findMovablePiece(player)
                .map(piece -> new MoveCommand(player, piece, rollValue, board));
    }

    private static Optional<Piece> findMovablePiece(Player player) {
        return player.getPieces().stream()
                .filter(piece -> !piece.isAtBase())
                .findFirst();
    }
}
