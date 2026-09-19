package model.player.rule;

import java.util.List;
import java.util.Optional;

import utils.random.CoinToss;
import config.constant.DiceConstants;
import model.player.command.Command;
import model.player.command.movement.EnterBoardCommand;
import model.board.Board;
import model.piece.Piece;
import model.player.Player;

// Rule 2: a piece may leave Base only when the dice shows a 6.
public final class BaseExitRule implements TurnRule {

    private final CoinToss coinToss;

    public BaseExitRule(CoinToss coinToss) {
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
