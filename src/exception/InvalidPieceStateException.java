package exception;


// Thrown when a piece is in a state where the requested action doesn't make sense.
// For example, asking for the track position of a piece that is still at Base.

public class InvalidPieceStateException extends IllegalStateException {

    /**
    Creates the exception with a message.
    @param message what went wrong
    */
    public InvalidPieceStateException(String message) {
        super(message);
    }
}
