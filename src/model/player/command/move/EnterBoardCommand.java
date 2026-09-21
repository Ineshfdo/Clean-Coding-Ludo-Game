package model.player.command.move;

import message.move.PieceDirectionAssigned;
import message.move.PieceEnteredBoard;
import message.observer.GameMessagePublisher;
import model.board.Board;
import model.direction.EntryDirection;
import model.direction.EntryDirectionAssigner;
import model.piece.Piece;
import model.player.Player;
import model.player.command.MoveCommand;
import utils.coin.CoinTossLabels;

// Rule 2: Base -> Entry, then a coin toss sets direction (T-1).
public final class EnterBoardCommand implements MoveCommand {

    private final Player player;
    private final Piece piece;

    private final Board board;
    private final EntryDirectionAssigner entryDirectionAssigner;

    public EnterBoardCommand(
            Player player, Piece piece, Board board, EntryDirectionAssigner entryDirectionAssigner) {
        this.player = player;
        this.piece = piece;
        this.board = board;
        this.entryDirectionAssigner = entryDirectionAssigner;
    }

    @Override
    public void execute(GameMessagePublisher messagePublisher) {
        player.leaveBase(piece, board);

        messagePublisher.publish(new PieceEnteredBoard(
                player.getColor(), piece.toString(), piece.getTrackPosition(),
                player.countPiecesOnBoard(), player.countPiecesAtBase()));

        EntryDirection entryDirection = entryDirectionAssigner.assign();
        player.assignMovementDirection(piece, entryDirection.getDirection());

        messagePublisher.publish(new PieceDirectionAssigned(
                piece.toString(), CoinTossLabels.labelOf(entryDirection.getTossResult()),
                entryDirection.getDirection().getLabel()));
    }

    @Override
    public boolean entersBoard() {
        return true;
    }

    @Override
    public Piece getAffectedPiece() {
        return piece;
    }
}
