package view.observer;
import service.result.GameMessage;

// Subject contract (Observer pattern): game logic publishes through this abstraction instead of calling observers directly.
public interface GameMessagePublisher {

    void addObserver(GameMessageObserver observer);

    void publish(GameMessage message);
}
