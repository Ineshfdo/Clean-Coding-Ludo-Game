package model.player.command.cannotmove;

import config.enums.CommandType;
import java.util.List;
import message.GameMessage;
import model.piece.Piece;
import model.player.command.Command;
import view.observer.GameMessagePublisher;

// T-3: announces an opponent blockade stops this piece or block.
public final class MoveBlockedByBlockadeCommand implements Command {

    private final Piece piece;
    private final List<Piece> blockPieces;

    public MoveBlockedByBlockadeCommand(List<Piece> blockPieces) {
        this.blockPieces = blockPieces;
        this.piece = blockPieces.get(0);
    }

    @Override
    public void execute(GameMessagePublisher messages) {
        messages.publish(GameMessage.pieceBlocked(describeLabel()));
    }

    // T-3: names the whole block (e.g. G1+G2), like MoveBlockCommand.
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
        return CommandType.CANNOT_MOVE;
    }

    @Override
    public Piece getAffectedPiece() {
        return piece;
    }
}
