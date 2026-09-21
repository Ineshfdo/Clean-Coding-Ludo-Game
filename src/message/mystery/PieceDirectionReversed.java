package message.mystery;

import message.GameMessage;

// T-14: a Gamma teleport reversed the piece/block's direction.
public record PieceDirectionReversed(
        String pieceLabel, String newDirectionLabel) implements GameMessage {
}
