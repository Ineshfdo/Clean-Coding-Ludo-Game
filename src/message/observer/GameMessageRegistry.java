package message.observer;

/**
Registration side of the message centre.
Only the start-up code of the game needs it, so the rules cannot add or remove observers.
*/
public interface GameMessageRegistry {

    /**
    Registers an observer that receives every message published from now on.
    @param observer the observer to add
    */
    void addObserver(GameMessageObserver observer);

    // Removes all observers, so that each game starts clean.
    
    void clearObservers();
}
