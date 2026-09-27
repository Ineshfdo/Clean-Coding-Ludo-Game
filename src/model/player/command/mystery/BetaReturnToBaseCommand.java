package model.player.command.mystery;

import java.util.List;
import message.mystery.BetaRestrictionTriggered;
import message.observer.GameMessagePublisher;
import model.piece.Piece;
import model.piece.PieceLabels;
import model.player.Player;
import model.player.command.Command;

/**
 * Sends a Beta-restricted piece or blockade back to Base after two rolls of 3 in a row (T-13).
 */
public final class BetaReturnToBaseCommand implements Command {

    private final Player player;
    private final List<Piece> returningPieces;

    /**
     * Creates the command.
     *
     * @param player the owner of the pieces
     * @param returningPieces the pieces that return to Base
     */
    public BetaReturnToBaseCommand(Player player, List<Piece> returningPieces) {
        this.player = player;
        this.returningPieces = returningPieces;
    }

    @Override
    public void execute(GameMessagePublisher messagePublisher) {
        for (Piece piece : returningPieces) {
            player.returnToBase(piece);
        }

        messagePublisher.publish(new BetaRestrictionTriggered(PieceLabels.joinPieceLabels(returningPieces)));
    }

    @Override
    public Piece getAffectedPiece() {
        return returningPieces.get(0);
    }

    @Override
    public List<Piece> getAffectedPieces() {
        return returningPieces;
    }
}
