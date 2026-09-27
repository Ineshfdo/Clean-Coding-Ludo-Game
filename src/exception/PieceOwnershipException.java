package exception;

/**
 * Thrown when a player is asked to act on a piece that belongs to another player.
 */
public class PieceOwnershipException extends IllegalArgumentException {

    /**
     * Creates the exception with a message.
     *
     * @param message what went wrong
     */
    public PieceOwnershipException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a message and the exception that caused it, so the stack trace of
     * the cause is kept.
     *
     * @param message what went wrong
     * @param cause the exception that caused this one
     */
    public PieceOwnershipException(String message, Throwable cause) {
        super(message, cause);
    }
}
