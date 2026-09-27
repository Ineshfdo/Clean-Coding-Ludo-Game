package model.player.command.move;

import config.constant.BoardConstants;
import java.util.List;
import java.util.Optional;
import message.GameMessage;
import message.move.BlockMoved;
import message.move.PieceEnteredHomeStraight;
import message.move.PieceReachedHome;
import message.observer.GameMessagePublisher;
import model.board.Board;
import model.direction.MovementDirectionStrategy;
import model.piece.Piece;
import model.piece.PieceLabels;
import model.player.HomeEntryPolicy;
import model.player.Player;
import model.player.command.MoveCommand;
import model.player.strategy.blockdirection.BlockDirectionClassifier;

/**
 * Moves a blockade as one unit, using one shared direction (T-3).
 */
public final class MoveBlockCommand implements MoveCommand {

    private final Player player;
    private final List<Piece> blockPieces;

    private final int effectiveSteps;
    private final Board board;
    private final HomeEntryPolicy homeEntryPolicy;
    private final MovementDirectionStrategy travelDirection;

    /**
     * Creates the command.
     *
     * @param player the owner of the pieces
     * @param blockPieces the pieces of the blockade
     * @param effectiveSteps the number of steps that every piece moves
     * @param board the board the pieces move on
     * @param homeEntryPolicy decides whether the pieces may enter their HomeStraight
     * @param travelDirection the direction in which the blockade travels
     */
    public MoveBlockCommand(Player player, List<Piece> blockPieces, int effectiveSteps,
            Board board, HomeEntryPolicy homeEntryPolicy,
            MovementDirectionStrategy travelDirection) {
        this.player = player;
        this.blockPieces = blockPieces;
        this.effectiveSteps = effectiveSteps;
        this.board = board;
        this.homeEntryPolicy = homeEntryPolicy;
        this.travelDirection = travelDirection;
    }

    @Override
    public void execute(GameMessagePublisher messagePublisher) {
        Piece representative = blockPieces.get(0);
        int fromPosition = representative.isOnTrack()
                ? representative.getTrackPosition() : BoardConstants.NO_TRACK_POSITION;

        for (Piece piece : blockPieces) {
            player.adoptBlockDirection(piece, travelDirection, blockPieces.size());
            player.moveForward(piece, effectiveSteps, board, homeEntryPolicy, travelDirection);
        }

        messagePublisher.publish(describeOutcome(fromPosition));
    }

    // Members move identically, so one piece describes the block.
    private GameMessage describeOutcome(int fromPosition) {
        Piece representative = blockPieces.get(0);
        String blockLabel = PieceLabels.joinPieceLabels(blockPieces);

        if (representative.isHome()) {
            return new PieceReachedHome(blockLabel);
        }

        if (representative.isOnHomeStraight()) {
            String cellLabel = board
                    .getHomeStraightCell(representative.getColor(), representative.getHomeStraightIndex())
                    .toString();

            return new PieceEnteredHomeStraight(blockLabel, cellLabel);
        }

        return new BlockMoved(
                blockLabel, fromPosition, representative.getTrackPosition(),
                BlockDirectionClassifier.labelOf(BlockDirectionClassifier.classify(blockPieces)),
                travelDirection.getLabel());
    }

    @Override
    public Piece getAffectedPiece() {
        return blockPieces.get(0);
    }

    @Override
    public Optional<Integer> previewLandingPosition() {
        return TrackLandingFinder.findLandingPosition(blockPieces.get(0), effectiveSteps, board, travelDirection);
    }

    @Override
    public boolean reachesHome() {
        return HomeArrivalChecker.reachesHome(
                blockPieces.get(0), effectiveSteps, board, homeEntryPolicy, travelDirection);
    }

    @Override
    public boolean leavesStandardPath() {
        return HomeArrivalChecker.leavesTrack(
                blockPieces.get(0), effectiveSteps, board, homeEntryPolicy, travelDirection);
    }

    // T-4/T-17: the "move as a block" action GreenStrategy prefers.
    @Override
    public boolean movesExistingBlock() {
        return true;
    }
}
