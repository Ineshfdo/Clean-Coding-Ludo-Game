package message.turn;

import config.enums.PlayerColor;
import message.GameMessage;

/**
 The home gate opened for a colour.
 No opponent pieces remain, so the capture requirement no longer applies to it (T-7).
 @param playerColor the colour for which the gate opened
 */
public record HomeGateOpened(PlayerColor playerColor) implements GameMessage {
}
