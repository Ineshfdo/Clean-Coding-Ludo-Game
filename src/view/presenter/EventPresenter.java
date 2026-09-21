package view.presenter;

import message.GameMessage;

// Turns one kind of game message into console text.
public abstract class EventPresenter<M extends GameMessage> {

    private final Class<M> eventType;

    protected EventPresenter(Class<M> eventType) {
        this.eventType = eventType;
    }

    public final Class<M> getEventType() {
        return eventType;
    }

    public abstract String present(M message);
}
