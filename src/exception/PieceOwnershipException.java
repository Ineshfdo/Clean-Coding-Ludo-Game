package exception;


// Thrown when a player tries to move or control a piece that isn't their.
 
public class PieceOwnershipException extends IllegalArgumentException {

    /**
    Creates the exception with a message.
    @param message what went wrong
    */
    public PieceOwnershipException(String message) {
        super(message);
    }

    /**
    Creates the exception with a message and the cause behind it.
    @param message what went wrong
    @param cause the exception that led to this one
    */
    public PieceOwnershipException(String message, Throwable cause) {
        super(message, cause);
    }
}