package exception;

import java.util.NoSuchElementException;

// No player of the requested color is in the game.
public class PlayerNotFoundException extends NoSuchElementException {

    public PlayerNotFoundException(String message) {
        super(message);
    }

    public PlayerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
