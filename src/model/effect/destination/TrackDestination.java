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

// T-11: a destination on the standard track; subclasses say which cell and what arriving does.
public abstract class TrackDestination implements MysteryCellDestination {

    private final EffectActivationRule effectActivationRule;

    protected TrackDestination(EffectActivationRule effectActivationRule) {
        this.effectActivationRule = effectActivationRule;
    }

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

    protected abstract int findCellPosition(Player player);

    protected void recordArrival(Player player, List<Piece> teleportedPieces) {
    }

    protected void applyEffect(
            Player player, List<Piece> teleportedPieces, GameMessagePublisher messagePublisher) {
    }
}
