package exception;

/**
 Thrown when a piece is asked for something its current location cannot give, for example the track position of a piece that is at Base.
 */
public class InvalidPieceStateException extends IllegalStateException {

    /**
     Creates the exception with a message.
     @param message what went wrong
     */
    public InvalidPieceStateException(String message) {
        super(message);
    }

    /**
     Creates the exception with a message and the exception that caused it, so the stack trace of the cause is kept.
     @param message what went wrong
     @param cause the exception that caused this one
     */
    public InvalidPieceStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
