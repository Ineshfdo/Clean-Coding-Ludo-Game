package model.player.command.move;

import config.constant.BoardConstants;
import config.enums.CommandType;
import java.util.Optional;
import message.GameMessage;
import message.observer.GameMessagePublisher;
import model.board.Board;
import model.direction.MovementDirectionStrategy;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.rule.home.HomeStraightEntryRule;

// Rule 1: moves a piece forward by the dice value.
public final class MovePieceCommand implements Command {

    private final Player player;
    private final Piece piece;

    private final int effectiveSteps;
    private final Board board;
    private final HomeStraightEntryRule homeStraightEntryRule;
    private final MovementDirectionStrategy travelDirection;

    public MovePieceCommand(
            Player player, Piece piece, int effectiveSteps, Board board,
            HomeStraightEntryRule homeStraightEntryRule, MovementDirectionStrategy travelDirection) {
        this.player = player;
        this.piece = piece;
        this.effectiveSteps = effectiveSteps;
        this.board = board;
        this.homeStraightEntryRule = homeStraightEntryRule;
        this.travelDirection = travelDirection;
    }

    @Override
    public void execute(GameMessagePublisher messagePublisher) {
        int fromPosition = piece.isOnTrack() ? piece.getTrackPosition() : BoardConstants.NO_TRACK_POSITION;

        player.moveForward(piece, effectiveSteps, board, homeStraightEntryRule, travelDirection);

        messagePublisher.publish(describeOutcome(fromPosition));
    }

    // Outcome: track landing, HomeStraight entry, or Home.
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
            effectiveSteps, travelDirection.getLabel());
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
        return TrackLandingFinder.findLandingPosition(piece, effectiveSteps, board, travelDirection);
    }

    @Override
    public boolean reachesHome() {
        return HomeArrivalChecker.reachesHome(piece, effectiveSteps, board, homeStraightEntryRule, travelDirection);
    }

    @Override
    public boolean leavesStandardPath() {
        return HomeArrivalChecker.leavesTrack(piece, effectiveSteps, board, homeStraightEntryRule, travelDirection);
    }
}
