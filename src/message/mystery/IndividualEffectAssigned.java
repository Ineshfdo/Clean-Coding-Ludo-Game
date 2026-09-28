package message.mystery;

import message.GameMessage;

/**
 A coin toss gave a piece its own Energized or Sick effect (T-12).
 @param pieceLabel the name of the piece
 @param effectLabel the effect: Energized or Sick
 */
public record IndividualEffectAssigned(
        String pieceLabel, String effectLabel) implements GameMessage {
}
