package command;

import gamemessage.GameMessage;
import gamemessage.GameMessagePublisher;
import ludoboard.Board;
import player.Piece;
import player.Player;

// Rule 1 as a command: move the selected piece by the effective dice value. Delegates to Player.moveForward(), which already owns ownership validation and the actual piece mutation - this class only represents the intention to make that move, and announces the result once it happens.
public final class MoveCommand implements Command {

    private final Player player;
    private final Piece piece;
    private final int effectiveDiceValue;
    private final Board board;

    public MoveCommand(Player player, Piece piece, int effectiveDiceValue, Board board) {
        this.player = player;
        this.piece = piece;
        this.effectiveDiceValue = effectiveDiceValue;
        this.board = board;
    }

    @Override
    public void execute(GameMessagePublisher messages) {
        player.moveForward(piece, effectiveDiceValue, board);
        messages.publish(GameMessage.pieceMoved(piece.toString(), piece.getTrackPosition()));
    }
}
