package model.effect.destination;

import java.util.List;
import message.mystery.BetaRestrictionApplied;
import message.observer.GameMessagePublisher;
import model.board.MysteryCellPositions;
import model.effect.activation.EffectActivationRule;
import model.effect.restriction.BetaRestrictedState;
import model.piece.Piece;
import model.piece.PieceLabels;
import model.player.Player;

/**
 * The Beta destination (T-13). The teleported pieces cannot move for four rounds.
 */
public final class BetaDestination extends TrackDestination {

    private static final String LABEL = "Beta";

    private final MysteryCellPositions positions;

    /**
     * Creates the Beta destination.
     *
     * @param positions gives the Beta cell
     * @param effectActivationRule decides whether the restriction may start
     */
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
                new BetaRestrictionApplied(PieceLabels.joinPieceLabels(teleportedPieces)));
    }
}
