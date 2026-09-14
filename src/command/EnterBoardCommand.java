package command;

import coin.CoinToss;
import coin.CoinTossResult;
import direction.ClockwiseMovementStrategy;
import direction.CounterClockwiseMovementStrategy;
import direction.MovementDirectionStrategy;
import gamemessage.GameMessage;
import gamemessage.GameMessagePublisher;
import ludoboard.Board;
import player.Piece;
import player.Player;

// Rule 2 as a command: Base -> Entry ("X"), then T-1's coin toss sets direction.
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
        messages.publish(GameMessage.pieceEnteredBoard(piece.toString(), piece.getTrackPosition()));

        CoinTossResult tossResult = coinToss.flip();
        MovementDirectionStrategy direction = resolveDirection(tossResult);
        player.assignMovementDirection(piece, direction);
        messages.publish(GameMessage.pieceDirectionAssigned(
                piece.toString(), tossResult.getLabel(), direction.getLabel()));
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
