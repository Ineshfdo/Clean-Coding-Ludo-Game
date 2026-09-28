package exception;

/**
 Thrown when a game message reaches the console but no presenter is registered for its type.
 */
public class UnpresentableMessageException extends IllegalStateException {

    /**
     Creates the exception with a message.
     @param message what went wrong
     */
    public UnpresentableMessageException(String message) {
        super(message);
    }

    /**
     Creates the exception with a message and the exception that caused it, so the stack trace of the cause is kept.
     @param message what went wrong
     @param cause the exception that caused this one
     */
    public UnpresentableMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
