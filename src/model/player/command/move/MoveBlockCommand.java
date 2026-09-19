package model.player.command.move;

import config.constant.BoardConstants;
import config.enums.CommandType;
import java.util.List;
import java.util.Optional;
import model.board.Board;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;
import model.player.rule.HomeStraightEntryRule;
import model.player.strategy.BlockDirectionClassifier;
import model.position.MovementDirectionStrategy;
import service.result.GameMessage;
import view.observer.GameMessagePublisher;

// T-3: a block moves together using one shared direction.
public final class MoveBlockCommand implements Command {

    private final Player player;
    private final List<Piece> blockPieces;

    private final int effectiveDiceValue;
    private final Board board;
    private final HomeStraightEntryRule homeStraightEntryRule;
    private final MovementDirectionStrategy travelDirection;

    public MoveBlockCommand(Player player, List<Piece> blockPieces, int effectiveDiceValue,
            Board board, HomeStraightEntryRule homeStraightEntryRule,
            MovementDirectionStrategy travelDirection) {
        this.player = player;
        this.blockPieces = blockPieces;
        this.effectiveDiceValue = effectiveDiceValue;
        this.board = board;
        this.homeStraightEntryRule = homeStraightEntryRule;
        this.travelDirection = travelDirection;
    }

    @Override
    public void execute(GameMessagePublisher messages) {
        Piece representative = blockPieces.get(0);
        int fromPosition = representative.isOnTrack()
                ? representative.getTrackPosition() : BoardConstants.NO_TRACK_POSITION;

        for (Piece piece : blockPieces) {
            player.adoptBlockDirection(piece, travelDirection, blockPieces.size());
            player.moveForward(piece, effectiveDiceValue, board, homeStraightEntryRule, travelDirection);
        }

        messages.publish(describeOutcome(fromPosition));
    }

    // Members move identically, so one piece describes the block.
    private GameMessage describeOutcome(int fromPosition) {
        Piece representative = blockPieces.get(0);
        String blockLabel = describeBlockLabel();

        if (representative.isHome()) {
            return GameMessage.pieceReachedHome(blockLabel);
        }

        if (representative.isOnHomeStraight()) {
            String cellLabel = board
                    .getHomeStraightCell(representative.getColor(), representative.getHomeStraightIndex())
                    .toString();

            return GameMessage.pieceEnteredHomeStraight(blockLabel, cellLabel);
        }

        return GameMessage.blockMoved(
                blockLabel, fromPosition, representative.getTrackPosition(),
                BlockDirectionClassifier.labelOf(BlockDirectionClassifier.classify(blockPieces)),
                travelDirection.getLabel());
    }

    private String describeBlockLabel() {
        StringBuilder label = new StringBuilder();

        for (Piece piece : blockPieces) {
            if (label.length() > 0) {
                label.append('+');
            }

            label.append(piece);
        }

        return label.toString();
    }

    @Override
    public CommandType getType() {
        return CommandType.MOVE_FORWARD;
    }

    @Override
    public Piece getAffectedPiece() {
        return blockPieces.get(0);
    }

    @Override
    public Optional<Integer> previewLandingPosition() {
        return TrackLandingFinder.resolve(blockPieces.get(0), effectiveDiceValue, board, travelDirection);
    }

    @Override
    public boolean reachesHome() {
        return HomeArrivalChecker.resolve(
                blockPieces.get(0), effectiveDiceValue, board, homeStraightEntryRule, travelDirection);
    }

    // T-4/T-17: the "move as a block" action GreenStrategy prefers.
    @Override
    public boolean movesExistingBlock() {
        return true;
    }
}
