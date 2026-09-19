package message.observer;

import java.util.ArrayList;
import java.util.List;
import message.GameMessage;

// Singleton: every part of the game publishes here, so all observers see it.
public final class GameMessageCenter implements GameMessagePublisher {

    private static final GameMessageCenter SHARED_INSTANCE = new GameMessageCenter();

    private final List<GameMessageObserver> observers;

    private GameMessageCenter() {
        this.observers = new ArrayList<>();
    }

    public static GameMessageCenter getInstance() {
        return SHARED_INSTANCE;
    }

    @Override
    public void addObserver(GameMessageObserver observer) {
        observers.add(observer);
    }

    // Clears observers so each game starts clean; the singleton outlives games.
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
