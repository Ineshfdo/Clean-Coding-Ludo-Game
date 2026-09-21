package model.effect.destination;

import java.util.List;
import message.GameMessage;
import message.observer.GameMessagePublisher;
import model.board.MysteryCellPositions;
import model.effect.activation.EffectActivationRule;
import model.effect.restriction.BetaRestrictedState;
import model.piece.Piece;
import model.piece.PieceLabels;
import model.player.Player;

// T-13: Beta stops the teleported pieces from moving for a while.
public final class BetaDestination extends TrackDestination {

    private static final String LABEL = "Beta";

    private final MysteryCellPositions positions;

    public BetaDestination(MysteryCellPositions positions, EffectActivationRule effectActivationRule) {
        super(effectActivationRule);
        this.positions = positions;
    }

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    protected int findCellPosition(Player player) {
        return positions.getBetaCellPosition();
    }

    @Override
    protected void applyEffect(
            Player player, List<Piece> teleportedPieces, GameMessagePublisher messagePublisher) {
        BetaRestrictedState restriction = new BetaRestrictedState();

        for (Piece piece : teleportedPieces) {
            player.applyRestriction(piece, restriction);
        }

        messagePublisher.publish(
                GameMessage.betaRestrictionApplied(PieceLabels.joinPieceLabels(teleportedPieces)));
    }
}
