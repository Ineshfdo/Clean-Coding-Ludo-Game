package message.move;

import message.GameMessage;

// T-4/T-13: a block move reports its direction, type and both endpoints.
public record BlockMoved(
        String blockLabel, int fromPosition, int newPosition, String blockTypeLabel,
        String movementDirectionLabel) implements GameMessage {
}
