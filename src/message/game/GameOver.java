package message.game;

import config.enums.PlayerColor;
import java.util.List;
import message.GameMessage;

/**
 * The game is over. The standings are ranked from first place in finishing order.
 *
 * @param finalStandings the colours of the players in finishing order
 */
public record GameOver(List<PlayerColor> finalStandings) implements GameMessage {

    /**
     * Copies the list, so that later changes to the list of the caller cannot change this event.
     */
    public GameOver {
        finalStandings = List.copyOf(finalStandings);
    }
}
