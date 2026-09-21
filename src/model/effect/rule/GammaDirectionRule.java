package model.effect.rule;

import config.enums.MysteryCellDestinationType;

import java.util.List;

import model.piece.PieceLabels;
import model.player.command.Command;
import model.player.command.mystery.MysteryCellTeleportCommand;
import model.direction.MovementDirectionStrategy;
import message.GameMessage;
import message.observer.GameMessagePublisher;
import model.board.Board;
import model.piece.Piece;
import model.player.Player;

// T-14: Clockwise groups reverse direction; Counter-Clockwise groups go on to Beta.
public final class GammaDirectionRule {

    private final Board board;

    public GammaDirectionRule(Board board) {
        this.board = board;
    }

    // T-15: reuses the same effects bundle, so Beta gets the same activation check.
    public void applyTo(
            Player player, List<Piece> teleportedPieces, GameMessagePublisher messagePublisher,
            MysteryCellEffectRules effectRules) {
        MovementDirectionStrategy currentDirection = teleportedPieces.get(0).getMovementDirection();

        if (currentDirection.isClockwise()) {
            reverseDirection(player, teleportedPieces, messagePublisher);
            return;
        }

        forwardToBeta(player, teleportedPieces, messagePublisher, effectRules);
    }

    // T-14: permanently reverses every piece's direction.
    private static void reverseDirection(
            Player player, List<Piece> teleportedPieces, GameMessagePublisher messagePublisher) {
        for (Piece piece : teleportedPieces) {
            player.reverseDirection(piece);
        }

        MovementDirectionStrategy newDirection = teleportedPieces.get(0).getMovementDirection();
        messagePublisher.publish(GameMessage.pieceDirectionReversed(
                PieceLabels.joinPieceLabels(teleportedPieces), newDirection.getLabel()));
    }

    // T-14: Counter-Clockwise groups teleport on to Beta.
    private void forwardToBeta(
            Player player, List<Piece> teleportedPieces, GameMessagePublisher messagePublisher,
            MysteryCellEffectRules effectRules) {
        Command forwardToBeta = new MysteryCellTeleportCommand(
                player, teleportedPieces, MysteryCellDestinationType.BETA, board, effectRules);

        forwardToBeta.execute(messagePublisher);
    }
}
