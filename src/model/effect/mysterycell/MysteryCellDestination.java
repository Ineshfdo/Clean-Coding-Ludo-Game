package model.effect.mysterycell;

import java.util.List;
import message.observer.GameMessagePublisher;
import model.piece.Piece;
import model.player.Player;

// T-11: one place a Mystery Cell can send pieces; each destination knows what its arrival does.
public interface MysteryCellDestination {

    String getLabel();

    void receive(Player player, List<Piece> teleportedPieces, GameMessagePublisher messagePublisher);
}
