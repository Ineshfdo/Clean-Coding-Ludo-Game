package gamemessage;

import java.util.ArrayList;
import java.util.List;

// Singleton: every part of the game publishes here, so
// all observers see every message.
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

    // Clears observers so a fresh game starts clean; this
    // Singleton persists across the whole JVM.
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
