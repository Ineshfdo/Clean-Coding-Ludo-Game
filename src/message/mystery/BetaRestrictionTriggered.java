package message.mystery;

import message.GameMessage;

// T-13: consecutive 3s sent a Beta-restricted piece/block back to Base.
public record BetaRestrictionTriggered(String pieceLabel) implements GameMessage {
}
