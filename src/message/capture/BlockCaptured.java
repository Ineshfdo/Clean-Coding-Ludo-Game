package message.capture;

import message.GameMessage;

// T-8: a blockade captured an equal-sized blockade.
public record BlockCaptured(
        String capturingBlockLabel, String capturedBlockLabel) implements GameMessage {
}
