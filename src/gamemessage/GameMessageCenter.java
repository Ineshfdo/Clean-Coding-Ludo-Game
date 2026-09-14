package gamemessage;

import java.util.ArrayList;
import java.util.List;

// Singleton: every part of the game (board setup, dice setup, players through this one center,so one set of observers sees every message in order.

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

    @Override
    public void publish(GameMessage message) {
        for (GameMessageObserver observer : observers) {
            observer.onGameMessage(message);
        }
    }
}
