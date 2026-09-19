package model.effect.rule;

import config.enums.MysteryCellDestinationType;

import java.util.List;
import java.util.stream.Collectors;

import model.player.command.Command;
import model.player.command.mystery.MysteryCellTeleportCommand;
import model.direction.MovementDirectionStrategy;
import service.result.GameMessage;
import view.observer.GameMessagePublisher;
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
            Player player, List<Piece> teleportedPieces, GameMessagePublisher messages,
            MysteryCellEffects effects) {
        MovementDirectionStrategy currentDirection = teleportedPieces.get(0).getMovementDirectionStrategy();

        if (currentDirection.isClockwise()) {
            reverseDirection(player, teleportedPieces, messages);
            return;
        }

        forwardToBeta(player, teleportedPieces, messages, effects);
    }

    // T-14: permanently reverses every piece's direction.
    private static void reverseDirection(
            Player player, List<Piece> teleportedPieces, GameMessagePublisher messages) {
        for (Piece piece : teleportedPieces) {
            player.reverseDirection(piece);
        }

        MovementDirectionStrategy newDirection = teleportedPieces.get(0).getMovementDirectionStrategy();
        messages.publish(GameMessage.pieceDirectionReversed(
                describeLabel(teleportedPieces), newDirection.getLabel()));
    }

    // T-14: Counter-Clockwise groups teleport on to Beta.
    private void forwardToBeta(
            Player player, List<Piece> teleportedPieces, GameMessagePublisher messages,
            MysteryCellEffects effects) {
        Command forwardToBeta = new MysteryCellTeleportCommand(
                player, teleportedPieces, MysteryCellDestinationType.BETA, board, effects);

        forwardToBeta.execute(messages);
    }

    private static String describeLabel(List<Piece> pieces) {
        return pieces.stream().map(Piece::toString).collect(Collectors.joining("+"));
    }
}
