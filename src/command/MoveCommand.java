package command;

import ludoboard.Board;
import player.Piece;
import player.Player;

// Rule 1 as a command: move the selected piece by the effective dice value.
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
    public void execute() {
        player.moveForward(piece, effectiveDiceValue, board);
    }
}
