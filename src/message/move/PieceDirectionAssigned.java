package message.move;

import message.GameMessage;

/**
 The coin toss gave a piece that has just left Base its direction (T-1).
 @param pieceLabel the name of the piece
 @param coinTossResultLabel the result of the coin toss: Heads or Tails
 @param movementDirectionLabel the direction that the piece got
 */
public record PieceDirectionAssigned(
        String pieceLabel, String coinTossResultLabel, String movementDirectionLabel) implements GameMessage {
}
