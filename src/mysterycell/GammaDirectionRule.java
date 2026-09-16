package mysterycell;

import java.util.List;
import java.util.stream.Collectors;

import command.Command;
import command.TeleportCommand;
import direction.MovementDirectionStrategy;
import gamemessage.GameMessage;
import gamemessage.GameMessagePublisher;
import ludoboard.Board;
import player.Piece;
import player.Player;

// T-14: a Clockwise piece/block teleported to Gamma has its direction reversed; an already
// Counter-Clockwise piece/block is instead forwarded on to Beta.
public final class GammaDirectionRule {

    private final Board board;

    public GammaDirectionRule(Board board) {
        this.board = board;
    }

    // T-15: receives the same MysteryCellEffects bundle TeleportCommand was given, so the
    // forwarded Beta teleport goes through the identical activation-rule check.
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

    // T-14/Strategy: every member's direction is permanently replaced with its reverse.
    private static void reverseDirection(
            Player player, List<Piece> teleportedPieces, GameMessagePublisher messages) {
        for (Piece piece : teleportedPieces) {
            player.reverseDirection(piece);
        }
        MovementDirectionStrategy newDirection = teleportedPieces.get(0).getMovementDirectionStrategy();
        messages.publish(GameMessage.pieceDirectionReversed(
                describeLabel(teleportedPieces), newDirection.getLabel()));
    }

    // T-14/Command: already Counter-Clockwise, so this group is teleported onward to Beta -
    // reusing TeleportCommand also reuses T-13's restriction, at no extra cost.
    private void forwardToBeta(
            Player player, List<Piece> teleportedPieces, GameMessagePublisher messages,
            MysteryCellEffects effects) {
        Command forwardToBeta = new TeleportCommand(
                player, teleportedPieces, MysteryCellDestinationType.BETA, board, effects);
        forwardToBeta.execute(messages);
    }

    private static String describeLabel(List<Piece> pieces) {
        return pieces.stream().map(Piece::toString).collect(Collectors.joining("+"));
    }
}
