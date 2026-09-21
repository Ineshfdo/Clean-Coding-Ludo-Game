package message.game;

import config.enums.PlayerColor;
import java.util.List;
import message.GameMessage;

// 3.1: announces a player's pieces before the game begins.
public record PlayerRosterAnnounced(
        PlayerColor playerColor, List<String> pieceLabels) implements GameMessage {

    public PlayerRosterAnnounced {
        pieceLabels = List.copyOf(pieceLabels);
    }
}
