package rule;

import command.Command;
import command.EnterBoardCommand;
import java.util.Optional;
import ludoboard.Board;
import player.Piece;
import player.Player;

// Rule 2: a piece may leave Base only when the dice shows a 6.
public final class BaseExitRule extends TurnRule {

    private static final int BASE_EXIT_ROLL_VALUE = 6;

    @Override
    protected Optional<Command> resolve(Player player, int rollValue, Board board) {
        if (rollValue != BASE_EXIT_ROLL_VALUE) {
            return Optional.empty();
        }
        return findBasePiece(player)
            .map(piece -> new EnterBoardCommand(player, piece, board));
    }

    private static Optional<Piece> findBasePiece(Player player) {
        return player.getPieces().stream()
            .filter(Piece::isAtBase)
            .findFirst();
    }
}
