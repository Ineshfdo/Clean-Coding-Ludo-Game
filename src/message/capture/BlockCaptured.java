package message.capture;

import message.GameMessage;

/**
 A blockade captured an opponent blockade of the same size (T-8).
 @param capturingBlockLabel the joined names of the capturing pieces
 @param capturedBlockLabel the joined names of the captured pieces
 */
public record BlockCaptured(
        String capturingBlockLabel, String capturedBlockLabel) implements GameMessage {
}
