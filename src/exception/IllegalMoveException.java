package exception;


// Thrown when a move breaks the game rules. 
// For example when a piece at Base or at Home is asked to move.
 
public class IllegalMoveException extends RuntimeException {

    /**
    Creates the exception with a message.
    @param message what went wrong
    */
    public IllegalMoveException(String message) {
        super(message);
    }
}
