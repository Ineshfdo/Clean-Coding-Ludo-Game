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
}