package exception;

// Thrown when a game message reaches the console but no presenter is registered for its type.

public class UnpresentableMessageException extends IllegalStateException {

    /**
    Creates the exception with a message.
    @param message what went wrong
    */
   
    public UnpresentableMessageException(String message) {
        super(message);
    }

    /**
    Creates the exception with a message and the cause behind it.
    @param message what went wrong
    @param cause the exception that led to this one
    */
    public UnpresentableMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
