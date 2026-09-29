package message.observer;

import message.GameMessage;

/**
Subject side of the Observer pattern.
Game classes publish messages through this interface and never talk to the observers directly.
*/
public interface GameMessagePublisher {

    /**
    Announces an event to every observer.
    @param message the event that happened
    */
    void publish(GameMessage message);
}
