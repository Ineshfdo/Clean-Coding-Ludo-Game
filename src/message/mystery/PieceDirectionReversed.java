package message.mystery;

import message.GameMessage;

/**
 A Gamma teleport reversed the direction of a piece or a blockade (T-14).
 @param pieceLabel the name of the piece, or the joined names of the blockade
 @param newDirectionLabel the direction after the reversal
 */
public record PieceDirectionReversed(
        String pieceLabel, String newDirectionLabel) implements GameMessage {
}
