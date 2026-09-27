package exception;

/**
 * Thrown when a move breaks the game rules, for example when a piece at Base or at Home is asked to
 * move, or when a strategy has no legal command to choose from.
 */
public class IllegalMoveException extends RuntimeException {

    /**
     * Creates the exception with a message.
     *
     * @param message what went wrong
     */
    public IllegalMoveException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a message and the exception that caused it, so the stack trace of
     * the cause is kept.
     *
     * @param message what went wrong
     * @param cause the exception that caused this one
     */
    public IllegalMoveException(String message, Throwable cause) {
        super(message, cause);
    }
}
