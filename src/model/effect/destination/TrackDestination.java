package model.effect.destination;

import java.util.List;
import message.mystery.PieceTeleported;
import message.observer.GameMessagePublisher;
import model.effect.activation.EffectActivationRule;
import model.effect.activation.MysteryCellArrival;
import model.effect.mysterycell.MysteryCellDestination;
import model.piece.Piece;
import model.piece.PieceLabels;
import model.player.Player;

/**
 * Base class of the destinations that lie on the shared track (T-11), written as a Template Method.
 * {@link #receive} is final and always does the same steps: it places the pieces on the cell,
 * records the arrival, announces the teleport, and applies the effect if the activation rule allows
 * it. A subclass only says which cell it is and what the arrival does.
 */
public abstract class TrackDestination implements MysteryCellDestination {

    private final EffectActivationRule effectActivationRule;

    /**
     * Creates a destination on the track.
     *
     * @param effectActivationRule decides whether the effect of the destination may start
     */
    protected TrackDestination(EffectActivationRule effectActivationRule) {
        this.effectActivationRule = effectActivationRule;
    }

    /**
     * Runs the fixed steps of an arrival. A subclass cannot change their order.
     *
     * @param player the owner of the pieces
     * @param teleportedPieces the pieces that landed on the Mystery Cell
     * @param messagePublisher where the events are announced
     */
    @Override
    public final void receive(
            Player player, List<Piece> teleportedPieces, GameMessagePublisher messagePublisher) {
        int cellPosition = findCellPosition(player);

        for (Piece piece : teleportedPieces) {
            player.teleportTo(piece, cellPosition);
        }

        recordArrival(player, teleportedPieces);

        messagePublisher.publish(new PieceTeleported(
                PieceLabels.joinPieceLabels(teleportedPieces), getLabel(), cellPosition));

        // T-15: effects activate only after a genuine teleport.
        if (effectActivationRule.permitsActivation(new MysteryCellArrival(this))) {
            applyEffect(player, teleportedPieces, messagePublisher);
        }
    }

    /**
     * Says on which cell the pieces arrive.
     *
     * @param player the owner of the pieces; some destinations depend on its colour
     * @return the track position of the destination cell
     */
    protected abstract int findCellPosition(Player player);

    /**
     * Hook for extra bookkeeping after the pieces are placed. It does nothing by default.
     *
     * @param player the owner of the pieces
     * @param teleportedPieces the pieces that have arrived
     */
    protected void recordArrival(Player player, List<Piece> teleportedPieces) {
    }

    /**
     * Hook for the effect of the destination. It does nothing by default.
     *
     * @param player the owner of the pieces
     * @param teleportedPieces the pieces that have arrived
     * @param messagePublisher where the events are announced
     */
    protected void applyEffect(
            Player player, List<Piece> teleportedPieces, GameMessagePublisher messagePublisher) {
    }
}
