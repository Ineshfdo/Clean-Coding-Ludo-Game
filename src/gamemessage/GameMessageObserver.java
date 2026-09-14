package gamemessage;

// Observer: implementations decide how a published GameMessage is shown.
public interface GameMessageObserver {

    void onGameMessage(GameMessage message);
}
