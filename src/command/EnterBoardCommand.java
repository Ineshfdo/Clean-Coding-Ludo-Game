package command;

import gamemessage.GameMessage;
import gamemessage.GameMessagePublisher;
import ludoboard.Board;
import player.Piece;
import player.Player;

// Rule 2 as a command: move the selected piece from Base onto its color's Entry ("X") cell. Delegates to Player.leaveBase(), which  already owns ownership validation and the actual piece mutation.
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
}
