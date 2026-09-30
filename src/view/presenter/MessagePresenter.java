package view.presenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exception.UnpresentableMessageException;
import message.GameMessage;

/**
 Presents any game message by finding the presenter that is registered for its type.
 */
public final class MessagePresenter {

    // Map: message type -> its presenter, found in one lookup.
    // ?: any message type; each presenter handles a different one.
    private final Map<Class<?>, EventPresenter<?>> presenters = new HashMap<>();

    /**
     Creates the presenter for all kinds of message.
     @param eventPresenters one presenter for each kind of message
     */
    public MessagePresenter(List<EventPresenter<? extends GameMessage>> eventPresenters) {
        // Registers each presenter under the message type it handles.
        for (EventPresenter<?> eventPresenter : eventPresenters) {
            presenters.put(eventPresenter.getEventType(), eventPresenter);
        }
    }

    /**
     Turns a message into console text.
     @param message the message to present
     @return the console text
     @throws UnpresentableMessageException if no presenter is registered for the type of the message
     */
    public String present(GameMessage message) {
        EventPresenter<?> presenter = presenters.get(message.getClass());

        // No presenter registered: fail loudly instead of printing nothing.
        if (presenter == null) {
            throw new UnpresentableMessageException(
                "No presenter is registered for " + message.getClass().getSimpleName());
        }

        return presentWith(presenter, message);
    }

    // Casts message to the presenter's own type, then presents it.
    private static <M extends GameMessage> String presentWith(EventPresenter<M> presenter, GameMessage message) {
        return presenter.present(presenter.getEventType().cast(message));
    }
}
