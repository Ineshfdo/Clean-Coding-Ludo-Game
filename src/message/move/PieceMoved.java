package message.move;

import config.enums.PlayerColor;
import message.GameMessage;

// Requirement 2: a solo move reports its dice value, direction and both endpoints.
public record PieceMoved(
        PlayerColor playerColor, String pieceLabel, int fromPosition, int newPosition,
        int rollValue, String movementDirectionLabel) implements GameMessage {
}
