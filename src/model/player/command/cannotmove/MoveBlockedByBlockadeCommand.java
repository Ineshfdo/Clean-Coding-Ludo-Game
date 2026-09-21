package model.player.command.cannotmove;

import config.enums.CommandType;
import java.util.List;
import message.GameMessage;
import message.observer.GameMessagePublisher;
import model.piece.Piece;
import model.piece.PieceLabels;
import model.player.command.Command;

// T-3: announces an opponent blockade stops this piece or block.
public final class MoveBlockedByBlockadeCommand implements Command {

    private final Piece piece;
    private final List<Piece> blockPieces;

    public MoveBlockedByBlockadeCommand(List<Piece> blockPieces) {
        this.blockPieces = blockPieces;
        this.piece = blockPieces.get(0);
    }

    @Override
    public void execute(GameMessagePublisher messagePublisher) {
        messagePublisher.publish(GameMessage.pieceBlocked(PieceLabels.joinPieceLabels(blockPieces)));
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
