package command;

import gamemessage.GameMessage;
import gamemessage.GameMessagePublisher;
import ludoboard.Board;
import player.Piece;
import player.Player;

// Rule 2 as a command: moves a piece from Base to
// its Entry ("X") cell.
public final class EnterBoardCommand implements Command {

    private final Player player;
    private final Piece piece;
    private final Board board;

    public EnterBoardCommand(Player player, Piece piece, Board board) {
        this.player = player;
        this.piece = piece;
        this.board = board;
    }

    @Override
    public void execute(GameMessagePublisher messages) {
        player.leaveBase(piece, board);
        messages.publish(GameMessage.pieceEnteredBoard(piece.toString(), piece.getTrackPosition()));
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
