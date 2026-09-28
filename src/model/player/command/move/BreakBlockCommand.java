package model.player.command.move;

import java.util.List;
import java.util.stream.Collectors;
import message.move.PieceLeftBlock;
import message.observer.GameMessagePublisher;
import model.piece.Piece;
import model.player.Player;
import model.player.command.MoveCommand;

/**
 Pieces leave a blockade and get their own direction back, and then each released piece runs its own move (T-5, T-6).
 */
public final class BreakBlockCommand implements MoveCommand {

    private final Player player;
    private final List<Piece> restoredPieces;
    private final List<MoveCommand> releasedPieceMoves;

    /**
     Creates the command.
     @param player the owner of the pieces
     @param restoredPieces the pieces that leave the blockade
     @param releasedPieceMoves the moves of the pieces that are released
     */
    public BreakBlockCommand(
        Player player, List<Piece> restoredPieces, List<MoveCommand> releasedPieceMoves) {
        this.player = player;
        this.restoredPieces = restoredPieces;
        this.releasedPieceMoves = releasedPieceMoves;
    }

    @Override
    public void execute(GameMessagePublisher messagePublisher) {
        for (Piece piece : restoredPieces) {
            player.restoreOriginalDirection(piece);
            messagePublisher.publish(new PieceLeftBlock(piece.toString()));
        }

        for (MoveCommand releasedPieceMove : releasedPieceMoves) {
            releasedPieceMove.execute(messagePublisher);
        }
    }

    @Override
    public Piece getAffectedPiece() {
        return restoredPieces.get(0);
    }

    // T-6: capture checks use the pieces that actually moved.
    @Override
    public List<Piece> getAffectedPieces() {
        if (releasedPieceMoves.isEmpty()) {
            return List.of(getAffectedPiece());
        }

        return releasedPieceMoves.stream()
            .map(MoveCommand::getAffectedPiece)
            .collect(Collectors.toList());
    }

    // T-17: the "leave the block" action GreenStrategy avoids.
    @Override
    public boolean breaksExistingBlock() {
        return true;
    }

    @Override
    public boolean leavesStandardPath() {
        return releasedPieceMoves.stream().anyMatch(MoveCommand::leavesStandardPath);
    }
}
