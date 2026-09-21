package message.observer;

import message.GameMessage;

// Subject: game logic publishes through this, never to observers directly.
public interface GameMessagePublisher {

    void publish(GameMessage message);
}
