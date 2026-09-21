package message.mystery;

import message.GameMessage;

// T-12: a coin toss gives the teleported block a shared Energized/Sick status.
public record BlockEffectAssigned(
        String blockLabel, String effectLabel) implements GameMessage {
}
