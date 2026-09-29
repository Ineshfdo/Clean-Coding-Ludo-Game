package exception;

import java.util.NoSuchElementException;

// Thrown when a player of the requested colour doesn't exist in the game.

public class PlayerNotFoundException extends NoSuchElementException {

    /**
    Creates the exception with a message.
    @param message what went wrong
    */
    public PlayerNotFoundException(String message) {
        super(message);
    }
}
