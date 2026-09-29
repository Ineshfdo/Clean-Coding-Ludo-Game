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
}
