package exception;

// A game message reached the console, but no presenter is registered for it.
public class UnpresentableMessageException extends IllegalStateException {

    public UnpresentableMessageException(String message) {
        super(message);
    }

    public UnpresentableMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
