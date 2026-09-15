package command;

import java.util.List;

import direction.MovementDirectionStrategy;
import gamemessage.GameMessage;
import gamemessage.GameMessagePublisher;
import ludoboard.Board;
import player.BlockDirectionType;
import player.HomeStraightEntryRule;
import player.Piece;
import player.Player;

// T-3 block moves together, using T-1's shared travelDirection - not each piece's own.
public final class BlockMoveCommand implements Command {

    private final Player player;
    private final List<Piece> blockPieces;
    private final int effectiveDiceValue;
    private final Board board;
    private final HomeStraightEntryRule homeStraightEntryRule;
    private final MovementDirectionStrategy travelDirection;

    public BlockMoveCommand(Player player, List<Piece> blockPieces, int effectiveDiceValue,
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
        for (Piece piece : blockPieces) {
            player.moveForward(piece, effectiveDiceValue, board, homeStraightEntryRule, travelDirection);
        }
        messages.publish(describeOutcome());
    }

    // Every member starts and moves identically, so one representative piece describes the block.
    private GameMessage describeOutcome() {
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
                blockLabel, representative.getTrackPosition(),
                BlockDirectionType.classify(blockPieces).getLabel(), travelDirection.getLabel());
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
}
