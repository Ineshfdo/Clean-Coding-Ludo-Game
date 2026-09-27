package model.player.command.mystery;

import java.util.List;
import message.observer.GameMessagePublisher;
import model.effect.mysterycell.MysteryCellDestination;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;

/**
 * Teleports pieces from the Mystery Cell to a chosen destination (T-11).
 */
public final class MysteryCellTeleportCommand implements Command {

    private final Player player;
    private final List<Piece> teleportedPieces;
    private final MysteryCellDestination destination;

    /**
     * Creates the command.
     *
     * @param player the owner of the pieces
     * @param teleportedPieces the pieces that landed on the Mystery Cell
     * @param destination the destination that receives the pieces
     */
    public MysteryCellTeleportCommand(
            Player player, List<Piece> teleportedPieces, MysteryCellDestination destination) {
        this.player = player;
        this.teleportedPieces = teleportedPieces;
        this.destination = destination;
    }

    @Override
    public void execute(GameMessagePublisher messagePublisher) {
        destination.receive(player, teleportedPieces, messagePublisher);
    }

    @Override
    public Piece getAffectedPiece() {
        return teleportedPieces.get(0);
    }

    @Override
    public List<Piece> getAffectedPieces() {
        return teleportedPieces;
    }
}
