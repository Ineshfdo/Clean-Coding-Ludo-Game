package command;

import direction.MovementDirectionStrategy;
import gamemessage.GameMessage;
import gamemessage.GameMessagePublisher;
import ludoboard.Board;
import player.HomeStraightEntryRule;
import player.Piece;
import player.Player;

// Rule 1 as a command: moves a piece forward by the
// dice's face value.
public final class MoveCommand implements Command {

    private final Player player;
    private final Piece piece;
    private final int effectiveDiceValue;
    private final Board board;
    private final HomeStraightEntryRule homeStraightEntryRule;
    private final MovementDirectionStrategy travelDirection;

    public MoveCommand(
            Player player, Piece piece, int effectiveDiceValue, Board board,
            HomeStraightEntryRule homeStraightEntryRule, MovementDirectionStrategy travelDirection) {
        this.player = player;
        this.piece = piece;
        this.effectiveDiceValue = effectiveDiceValue;
        this.board = board;
        this.homeStraightEntryRule = homeStraightEntryRule;
        this.travelDirection = travelDirection;
    }

    @Override
    public void execute(GameMessagePublisher messages) {
        player.moveForward(piece, effectiveDiceValue, board, homeStraightEntryRule, travelDirection);
        messages.publish(describeOutcome());
    }

    // The move may land back on the track, onto HomeStraight, or send it Home.
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

    @Override
    public Piece getAffectedPiece() {
        return piece;
    }
}
