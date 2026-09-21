package message.turn;

import config.enums.PlayerColor;
import message.GameMessage;

// Home gate: no opponent pieces remain, so the T-7 capture requirement is waived.
public record HomeGateOpened(PlayerColor playerColor) implements GameMessage {
}
