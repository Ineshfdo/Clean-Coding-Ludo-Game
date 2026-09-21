package message.cannotmove;

import message.GameMessage;

// T-12: a Sick effect cut the roll to zero cells.
public record EffectRollTooSmall(String pieceLabel) implements GameMessage {
}
