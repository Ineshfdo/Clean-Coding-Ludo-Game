package view.observer;
import message.GameMessage;

// Observer: implementations decide how a published GameMessage is shown.
public interface GameMessageObserver {

    void onGameMessage(GameMessage message);
}
