package message.mystery;

import message.GameMessage;

// T-13: a teleported piece/block cannot move for 4 rounds.
public record BetaRestrictionApplied(String pieceLabel) implements GameMessage {
}
