package message.observer;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import config.enums.PlayerColor;
import message.turn.TurnStarted;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("GameMessageCenter")
class GameMessageCenterTest {

    private final GameMessageCenter center = GameMessageCenter.getInstance();
    private final TurnStarted message = new TurnStarted(PlayerColor.RED);

    // The center is a singleton, so every test starts and ends with no observers.
    @BeforeEach
    @AfterEach
    void clearSharedObservers() {
        center.clearObservers();
    }

    @Test
    @DisplayName("publish hands the message to a registered observer")
    void publishDeliversToObserver() {
        GameMessageObserver observer = mock(GameMessageObserver.class);
        center.addObserver(observer);

        center.publish(message);

        verify(observer).onGameMessage(message);
    }

    @Test
    @DisplayName("publish reaches every registered observer")
    void publishDeliversToEveryObserver() {
        GameMessageObserver first = mock(GameMessageObserver.class);
        GameMessageObserver second = mock(GameMessageObserver.class);
        center.addObserver(first);
        center.addObserver(second);

        center.publish(message);

        verify(first).onGameMessage(message);
        verify(second).onGameMessage(message);
    }

    @Test
    @DisplayName("clearObservers stops later messages reaching old observers")
    void clearObserversRemovesEveryObserver() {
        GameMessageObserver observer = mock(GameMessageObserver.class);
        center.addObserver(observer);

        center.clearObservers();
        center.publish(message);

        verify(observer, never()).onGameMessage(message);
    }

    @Test
    @DisplayName("getInstance always returns the same shared center")
    void getInstanceReturnsSingleton() {
        assertSame(center, GameMessageCenter.getInstance());
    }
}
