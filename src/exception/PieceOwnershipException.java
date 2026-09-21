package exception;

// A player was asked to act on a piece it does not own.
public class PieceOwnershipException extends IllegalArgumentException {

    public PieceOwnershipException(String message) {
        super(message);
    }

    public PieceOwnershipException(String message, Throwable cause) {
        super(message, cause);
    }
}
