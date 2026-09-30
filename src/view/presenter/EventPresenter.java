package view.presenter;

import message.GameMessage;

/**
 Turns one kind of game message into console text.
 @param <M> the kind of message that this presenter handles
 */
public abstract class EventPresenter<M extends GameMessage> {

    // abstract: template only; each message type has its own child.
    // M must be a GameMessage, so children need no casting.
    // Remembers the message type handled; generics vanish at runtime.
    private final Class<M> eventType;

    /**
     Creates the presenter.
     @param eventType the class of the message that this presenter handles
     */
    protected EventPresenter(Class<M> eventType) {
        // protected: only child presenters call this, through super().
        this.eventType = eventType;
    }

    /**
     Tells which messages this presenter handles.
     @return the class of the message
     */
    public final Class<M> getEventType() {
        // final: children cannot change which message they handle.
        return eventType;
    }

    /**
     Writes the console text of a message.
     @param message the message to present
     @return the console text
     */
    public abstract String present(M message);
}
