package message.observer;

import message.GameMessage;

/**
Observer side of the Observer pattern.
An implementation decides how a published message is shown, for example by printing it on the console.
*/
public interface GameMessageObserver {

    /**
    Called for every published message.
    @param message the event that happened
    */
    void onGameMessage(GameMessage message);
}
