package model.player.command.move;

import config.constant.BoardConstants;
import config.enums.CommandType;
import java.util.Optional;
import model.board.Board;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.rule.HomeStraightEntryRule;
import model.position.MovementDirectionStrategy;
import service.result.GameMessage;
import view.observer.GameMessagePublisher;

// Rule 1: moves a piece forward by the dice value.
public final class MovePieceCommand implements Command {

    private final Player player;
    private final Piece piece;

    private final int effectiveDiceValue;
    private final Board board;
    private final HomeStraightEntryRule homeStraightEntryRule;
    private final MovementDirectionStrategy travelDirection;

    public MovePieceCommand(
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
        int fromPosition = piece.isOnTrack() ? piece.getTrackPosition() : BoardConstants.NO_TRACK_POSITION;

        player.moveForward(piece, effectiveDiceValue, board, homeStraightEntryRule, travelDirection);

        messages.publish(describeOutcome(fromPosition));
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
        return TrackLandingFinder.resolve(piece, effectiveDiceValue, board, travelDirection);
    }

    @Override
    public boolean reachesHome() {
        return HomeArrivalChecker.resolve(piece, effectiveDiceValue, board, homeStraightEntryRule, travelDirection);
    }
}
