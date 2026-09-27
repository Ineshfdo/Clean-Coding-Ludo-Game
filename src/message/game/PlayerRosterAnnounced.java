package message.game;

import config.enums.PlayerColor;
import java.util.List;
import message.GameMessage;

/**
 * The pieces of a player are announced before the game begins (3.1).
 *
 * @param playerColor the colour of the player
 * @param pieceLabels the names of the pieces of the player, for example R1 to R4
 */
public record PlayerRosterAnnounced(
        PlayerColor playerColor, List<String> pieceLabels) implements GameMessage {

    /**
     * Copies the list, so that later changes to the list of the caller cannot change this event.
     */
    public PlayerRosterAnnounced {
        pieceLabels = List.copyOf(pieceLabels);
    }
}
