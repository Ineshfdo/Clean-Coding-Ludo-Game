package rule;

import java.util.List;
import java.util.Optional;

import coin.CoinToss;
import command.Command;
import command.EnterBoardCommand;
import ludoboard.Board;
import player.Piece;
import player.Player;

// Rule 2: a piece may leave Base only when the dice shows a 6.
public final class BaseExitRule implements TurnRule {

    private static final int BASE_EXIT_ROLL_VALUE = 6;

    private final CoinToss coinToss;

    public BaseExitRule(CoinToss coinToss) {
        this.coinToss = coinToss;
    }

    @Override
    public List<Command> resolve(
            Player player, int rollValue, Board board, List<Player> allPlayers) {
        if (rollValue != BASE_EXIT_ROLL_VALUE) {
            return List.of();
        }
        return findBasePiece(player)
                .<List<Command>>map(piece -> List.of(new EnterBoardCommand(player, piece, board, coinToss)))
                .orElse(List.of());
    }

    private static Optional<Piece> findBasePiece(Player player) {
        return player.getPieces().stream()
                .filter(Piece::isAtBase)
                .findFirst();
    }
}
