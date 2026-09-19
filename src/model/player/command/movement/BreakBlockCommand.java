package model.player.command.movement;
import model.player.command.Command;
import config.enums.CommandType;

import java.util.List;
import java.util.stream.Collectors;

import service.result.GameMessage;
import view.observer.GameMessagePublisher;
import model.piece.Piece;
import model.player.Player;

// T-5/T-6: restores each piece's own direction; released pieces then run their own move.
public final class BreakBlockCommand implements Command {

    private final Player player;
    private final List<Piece> restoredPieces;
    private final List<Command> releasedPieceMoves;

    public BreakBlockCommand(
            Player player, List<Piece> restoredPieces, List<Command> releasedPieceMoves) {
        this.player = player;
        this.restoredPieces = restoredPieces;
        this.releasedPieceMoves = releasedPieceMoves;
    }

    @Override
    public void execute(GameMessagePublisher messages) {
        for (Piece piece : restoredPieces) {
            player.restoreOriginalDirection(piece);
            messages.publish(GameMessage.pieceLeftBlock(piece.toString()));
        }
        for (Command releasedPieceMove : releasedPieceMoves) {
            releasedPieceMove.execute(messages);
        }
    }

    @Override
    public CommandType getType() {
        return CommandType.MOVE_FORWARD;
    }

    @Override
    public Piece getAffectedPiece() {
        return restoredPieces.get(0);
    }

    // T-6: capture checks belong to whichever pieces actually moved, not the one left behind.
    @Override
    public List<Piece> getAffectedPieces() {
        if (releasedPieceMoves.isEmpty()) {
            return List.of(getAffectedPiece());
        }
        return releasedPieceMoves.stream()
                .map(Command::getAffectedPiece)
                .collect(Collectors.toList());
    }

    // T-17: this command IS the "leave the block" action GreenStrategy avoids unless forced.
    @Override
    public boolean breaksExistingBlock() {
        return true;
    }
}
