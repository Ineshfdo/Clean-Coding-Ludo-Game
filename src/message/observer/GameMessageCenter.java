package message.observer;

import java.util.ArrayList;
import java.util.List;

import message.GameMessage;

/**
The message centre of the game (Singleton).
Every part of the game publishes here, so all registered observers see every message.
*/
public final class GameMessageCenter implements GameMessagePublisher, GameMessageRegistry {

    private static final GameMessageCenter SHARED_INSTANCE = new GameMessageCenter();

    private final List<GameMessageObserver> observers;

    private GameMessageCenter() {
        this.observers = new ArrayList<>();
    }

    /**
    Gives the one shared message centre.
    @return the shared instance
    */
    public static GameMessageCenter getInstance() {
        return SHARED_INSTANCE;
    }

    @Override
    public void addObserver(GameMessageObserver observer) {
        observers.add(observer);
    }

    // Clears observers so each game starts clean; the singleton outlives games.
    @Override
    public void clearObservers() {
        observers.clear();
    }

    @Override
    public void publish(GameMessage message) {
        for (GameMessageObserver observer : observers) {
            observer.onGameMessage(message);
        }
    }
}
