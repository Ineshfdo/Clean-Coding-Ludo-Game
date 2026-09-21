package model.effect.rule;

import java.util.List;
import message.mystery.PieceDirectionReversed;
import message.observer.GameMessagePublisher;
import model.direction.MovementDirectionStrategy;
import model.effect.mysterycell.MysteryCellDestination;
import model.piece.Piece;
import model.piece.PieceLabels;
import model.player.Player;

// T-14: Clockwise groups reverse direction; Counter-Clockwise groups go on to Beta.
public final class GammaDirectionRule {

    private final MysteryCellDestination betaDestination;

    public GammaDirectionRule(MysteryCellDestination betaDestination) {
        this.betaDestination = betaDestination;
    }

    public void applyTo(
            Player player, List<Piece> teleportedPieces, GameMessagePublisher messagePublisher) {
        MovementDirectionStrategy currentDirection = teleportedPieces.get(0).getMovementDirection();

        if (currentDirection.isClockwise()) {
            reverseDirection(player, teleportedPieces, messagePublisher);
            return;
        }

        // T-14: Counter-Clockwise groups teleport on to Beta.
        betaDestination.receive(player, teleportedPieces, messagePublisher);
    }

    // T-14: permanently reverses every piece's direction.
    private static void reverseDirection(
            Player player, List<Piece> teleportedPieces, GameMessagePublisher messagePublisher) {
        for (Piece piece : teleportedPieces) {
            player.reverseDirection(piece);
        }

        MovementDirectionStrategy newDirection = teleportedPieces.get(0).getMovementDirection();
        messagePublisher.publish(new PieceDirectionReversed(
                PieceLabels.joinPieceLabels(teleportedPieces), newDirection.getLabel()));
    }
}
