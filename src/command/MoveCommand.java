package command;

import gamemessage.GameMessage;
import gamemessage.GameMessagePublisher;
import ludoboard.Board;
import player.Piece;
import player.Player;

// Rule 1 as a command: moves a piece forward by the
// dice's face value.
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
        messages.publish(describeOutcome());
    }

    // The same move can land the piece back on the track, onto its HomeStraight, or send it Home - each needs its own announcement.
    private GameMessage describeOutcome() {
        if (piece.isHome()) {
            return GameMessage.pieceReachedHome(piece.toString());
        }
        if (piece.isOnHomeStraight()) {
            String cellLabel =
                    board.getHomeStraightCell(piece.getColor(), piece.getHomeStraightIndex())
                            .toString();
            return GameMessage.pieceEnteredHomeStraight(piece.toString(), cellLabel);
        }
        return GameMessage.pieceMoved(piece.toString(), piece.getTrackPosition());
    }

    @Override
    public CommandType getType() {
        return CommandType.MOVE_FORWARD;
    }
}
