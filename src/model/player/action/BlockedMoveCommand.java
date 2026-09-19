package model.player.action;
import config.enums.CommandType;

import java.util.List;

import service.result.GameMessage;
import view.observer.GameMessagePublisher;
import model.piece.Piece;

// T-3: announces an opponent blockade fully preventing this piece (or its whole block) from moving.
public final class BlockedMoveCommand implements Command {

    private final Piece piece;
    private final List<Piece> blockPieces;

    public BlockedMoveCommand(List<Piece> blockPieces) {
        this.blockPieces = blockPieces;
        this.piece = blockPieces.get(0);
    }

    @Override
    public void execute(GameMessagePublisher messages) {
        messages.publish(GameMessage.pieceBlocked(describeLabel()));
    }

    // T-3: same-cell pieces are blocked together, so the message names the whole block ("G1+G2"),
    // not just the representative - matching BlockMoveCommand's label style.
    private String describeLabel() {
        StringBuilder label = new StringBuilder();
        for (Piece member : blockPieces) {
            if (label.length() > 0) {
                label.append('+');
            }
            label.append(member);
        }
        return label.toString();
    }

    @Override
    public CommandType getType() {
        return CommandType.BLOCKED;
    }

    @Override
    public Piece getAffectedPiece() {
        return piece;
    }
}
