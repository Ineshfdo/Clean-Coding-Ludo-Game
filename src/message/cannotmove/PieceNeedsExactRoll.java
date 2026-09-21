package message.cannotmove;

import message.GameMessage;

// Rule 10: an exact roll is needed to reach Home.
public record PieceNeedsExactRoll(String pieceLabel) implements GameMessage {
}
