package message.turn;

import message.GameMessage;

/**
 * Three sixes in a row: the roll is void and the turn passes.
 */
public record ThirdSixVoided() implements GameMessage {
}
