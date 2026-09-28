package view.presenter;

import exception.UnpresentableMessageException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import message.GameMessage;

/**
 Presents any game message by finding the presenter that is registered for its type.
 */
public final class MessagePresenter {

    private final Map<Class<? extends GameMessage>, EventPresenter<? extends GameMessage>> presenters =
            new HashMap<>();

    /**
     Creates the presenter for all kinds of message.
     @param eventPresenters one presenter for each kind of message
     */
    public MessagePresenter(List<EventPresenter<? extends GameMessage>> eventPresenters) {
        for (EventPresenter<? extends GameMessage> eventPresenter : eventPresenters) {
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
        EventPresenter<? extends GameMessage> presenter = presenters.get(message.getClass());

        if (presenter == null) {
            throw new UnpresentableMessageException(
                    "No presenter is registered for " + message.getClass().getSimpleName());
        }

        return presentWith(presenter, message);
    }

    private static <M extends GameMessage> String presentWith(EventPresenter<M> presenter, GameMessage message) {
        return presenter.present(presenter.getEventType().cast(message));
    }
}
