package model.player.command.move;

import config.enums.CoinTossResult;
import config.enums.CommandType;
import message.GameMessage;
import model.board.Board;
import model.direction.ClockwiseMovementStrategy;
import model.direction.CounterClockwiseMovementStrategy;
import model.direction.MovementDirectionStrategy;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import utils.random.CoinToss;
import utils.random.CoinTossLabels;
import view.observer.GameMessagePublisher;

// Rule 2: Base -> Entry, then a coin toss sets direction (T-1).
public final class EnterBoardCommand implements Command {

    private final Player player;
    private final Piece piece;

    private final Board board;
    private final CoinToss coinToss;

    public EnterBoardCommand(Player player, Piece piece, Board board, CoinToss coinToss) {
        this.player = player;
        this.piece = piece;
        this.board = board;
        this.coinToss = coinToss;
    }

    @Override
    public void execute(GameMessagePublisher messages) {
        player.leaveBase(piece, board);

        messages.publish(GameMessage.pieceEnteredBoard(
                player.getColor(), piece.toString(), piece.getTrackPosition(),
                player.countPiecesOnBoard(), player.countPiecesAtBase()));

        CoinTossResult tossResult = coinToss.flip();
        MovementDirectionStrategy direction = resolveDirection(tossResult);
        player.assignMovementDirection(piece, direction);

        messages.publish(GameMessage.pieceDirectionAssigned(
                piece.toString(), CoinTossLabels.labelOf(tossResult), direction.getLabel()));
    }

    private static MovementDirectionStrategy resolveDirection(CoinTossResult tossResult) {
        return tossResult == CoinTossResult.HEADS
                ? ClockwiseMovementStrategy.getInstance()
                : CounterClockwiseMovementStrategy.getInstance();
    }

    @Override
    public CommandType getType() {
        return CommandType.ENTER_BOARD;
    }

    @Override
    public Piece getAffectedPiece() {
        return piece;
    }
}
