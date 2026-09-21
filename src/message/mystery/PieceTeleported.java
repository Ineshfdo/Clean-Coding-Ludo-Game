package message.mystery;

import message.GameMessage;

// T-11: teleport to a random destination; newPosition is -1 for Base (no track cell).
public record PieceTeleported(
        String pieceLabel, String destinationLabel, int newPosition) implements GameMessage {
}
