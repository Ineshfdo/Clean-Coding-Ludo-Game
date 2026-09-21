package message.observer;

// Registration side of the message center: only the game start-up needs it.
public interface GameMessageRegistry {

    void addObserver(GameMessageObserver observer);

    void clearObservers();
}
