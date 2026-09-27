package exception;

import java.util.NoSuchElementException;

/**
 * Thrown when no player of the requested colour is in the game.
 */
public class PlayerNotFoundException extends NoSuchElementException {

    /**
     * Creates the exception with a message.
     *
     * @param message what went wrong
     */
    public PlayerNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a message and the exception that caused it, so the stack trace of
     * the cause is kept.
     *
     * @param message what went wrong
     * @param cause the exception that caused this one
     */
    public PlayerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
