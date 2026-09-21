package model.player.command.mystery;

import java.util.List;
import message.observer.GameMessagePublisher;
import model.effect.mysterycell.MysteryCellDestination;
import model.piece.Piece;
import model.player.Player;
import model.player.command.Command;

// T-11: teleports pieces from the Mystery Cell to a random destination.
public final class MysteryCellTeleportCommand implements Command {

    private final Player player;
    private final List<Piece> teleportedPieces;
    private final MysteryCellDestination destination;

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
