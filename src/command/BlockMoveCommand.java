package command;

import java.util.List;

import gamemessage.GameMessage;
import gamemessage.GameMessagePublisher;
import ludoboard.Board;
import player.Piece;
import player.Player;

// T-3: a block (2+ same-color pieces sharing a cell) moves together.
// Every member gets the same effective steps, so they land on the
// same new cell and stay paired.
public final class BlockMoveCommand implements Command {

    private final Player player;
    private final List<Piece> blockPieces;
    private final int effectiveDiceValue;
    private final Board board;

    public BlockMoveCommand(Player player, List<Piece> blockPieces, int effectiveDiceValue,
            Board board) {
        this.player = player;
        this.blockPieces = blockPieces;
        this.effectiveDiceValue = effectiveDiceValue;
        this.board = board;
    }

    @Override
    public void execute(GameMessagePublisher messages) {
        for (Piece piece : blockPieces) {
            player.moveForward(piece, effectiveDiceValue, board);
        }
        messages.publish(describeOutcome());
    }

    // Every member ends up identically placed (same start, same
    // steps), so one representative piece describes the whole block.
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
        return GameMessage.pieceMoved(blockLabel, representative.getTrackPosition());
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
