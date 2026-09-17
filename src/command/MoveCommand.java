package command;

import java.util.Optional;

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

    private static final int NO_PREVIOUS_POSITION = -1;

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
        int fromPosition = piece.isOnTrack() ? piece.getTrackPosition() : NO_PREVIOUS_POSITION;
        player.moveForward(piece, effectiveDiceValue, board, homeStraightEntryRule, travelDirection);
        messages.publish(describeOutcome(fromPosition));
    }

    // The move may land back on the track, onto HomeStraight, or send it Home.
    private GameMessage describeOutcome(int fromPosition) {
        if (piece.isHome()) {
            return GameMessage.pieceReachedHome(piece.toString());
        }
        if (piece.isOnHomeStraight()) {
            String cellLabel =
                    board.getHomeStraightCell(piece.getColor(), piece.getHomeStraightIndex())
                            .toString();
            return GameMessage.pieceEnteredHomeStraight(piece.toString(), cellLabel);
        }
        return GameMessage.pieceMoved(
                player.getColor(), piece.toString(), fromPosition, piece.getTrackPosition(),
                effectiveDiceValue, travelDirection.getLabel());
    }

    @Override
    public CommandType getType() {
        return CommandType.MOVE_FORWARD;
    }

    @Override
    public Piece getAffectedPiece() {
        return piece;
    }

    @Override
    public Optional<Integer> previewLandingPosition() {
        return TrackLandingPreview.resolve(piece, effectiveDiceValue, board, travelDirection);
    }

    @Override
    public boolean reachesHome() {
        return HomeArrivalPreview.resolve(piece, effectiveDiceValue, board, homeStraightEntryRule, travelDirection);
    }
}
