package message.mystery;

import message.GameMessage;

// T-12: a coin toss gives this piece its own Energized/Sick status.
public record IndividualEffectAssigned(
        String pieceLabel, String effectLabel) implements GameMessage {
}
