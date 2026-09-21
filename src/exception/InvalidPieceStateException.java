package exception;

// A piece was asked for something its current location cannot give.
public class InvalidPieceStateException extends IllegalStateException {

    public InvalidPieceStateException(String message) {
        super(message);
    }

    public InvalidPieceStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
