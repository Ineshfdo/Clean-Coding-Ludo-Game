package model.player.rule.turn;

import config.constant.DiceConstants;
import java.util.List;
import java.util.Optional;
import model.board.Board;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.command.move.EnterBoardCommand;
import utils.random.CoinToss;

// Rule 2: a piece may leave Base only on a 6.
public final class EnterBoardRule implements TurnRule {

    private final CoinToss coinToss;

    public EnterBoardRule(CoinToss coinToss) {
        this.coinToss = coinToss;
    }

    @Override
    public List<Command> resolve(
            Player player, int rollValue, Board board, List<Player> allPlayers) {
        if (rollValue != DiceConstants.SIX_ROLL_VALUE) {
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
