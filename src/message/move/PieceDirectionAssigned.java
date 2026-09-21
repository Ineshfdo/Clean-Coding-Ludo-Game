package message.move;

import message.GameMessage;

// T-1: the coin toss gave a piece that just left Base its direction.
public record PieceDirectionAssigned(
        String pieceLabel, String coinTossResultLabel, String movementDirectionLabel) implements GameMessage {
}
