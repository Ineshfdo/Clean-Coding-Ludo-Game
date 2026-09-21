package message.game;

import config.enums.PlayerColor;
import java.util.List;
import message.GameMessage;

// GAME_OVER: standings ranked 1st..4th in finishing order.
public record GameOver(List<PlayerColor> finalStandings) implements GameMessage {

    public GameOver {
        finalStandings = List.copyOf(finalStandings);
    }
}
