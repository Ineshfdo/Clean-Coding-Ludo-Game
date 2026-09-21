package model.player.command.mystery;

import java.util.List;
import message.GameMessage;
import message.observer.GameMessagePublisher;
import model.piece.Piece;
import model.piece.PieceLabels;
import model.player.Player;
import model.player.command.Command;

// T-13: consecutive 3s send a Beta-restricted piece/block back to Base.
public final class BetaReturnToBaseCommand implements Command {

    private final Player player;
    private final List<Piece> returningPieces;

    public BetaReturnToBaseCommand(Player player, List<Piece> returningPieces) {
        this.player = player;
        this.returningPieces = returningPieces;
    }

    @Override
    public void execute(GameMessagePublisher messagePublisher) {
        for (Piece piece : returningPieces) {
            player.returnToBase(piece);
        }

        messagePublisher.publish(GameMessage.betaRestrictionTriggered(PieceLabels.joinPieceLabels(returningPieces)));
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
