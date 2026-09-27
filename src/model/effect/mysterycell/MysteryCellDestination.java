package model.effect.mysterycell;

import java.util.List;
import message.observer.GameMessagePublisher;
import model.piece.Piece;
import model.player.Player;

/**
 * One place to which a Mystery Cell can send pieces (T-11). Each destination knows what happens
 * when the pieces arrive: for example Alpha gives an effect, Beta restricts the pieces and Gamma
 * reverses their direction.
 */
public interface MysteryCellDestination {

    /**
     * Gives the name of the destination.
     *
     * @return the name, for example Alpha
     */
    String getLabel();

    /**
     * Moves the pieces to this destination, announces the teleport and applies the effect of the
     * destination.
     *
     * @param player the owner of the pieces
     * @param teleportedPieces the pieces that landed on the Mystery Cell: one piece or a whole
     *     blockade
     * @param messagePublisher where the events are announced
     */
    void receive(Player player, List<Piece> teleportedPieces, GameMessagePublisher messagePublisher);
}
