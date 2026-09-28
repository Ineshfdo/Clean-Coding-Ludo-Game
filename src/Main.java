import controller.GameFacade;

/**
 Program entry point of LUDO-T.
 It starts one complete game with a fixed seed, so every run plays the same game and prints the final standings at the end.
 */
public class Main {

    private static final long GAME_SEED = 9L;

    /**
     Starts the game.
     Nothing is read from the user, because every player is controlled by a rule based strategy.
     @param args not used
     */
    public static void main(String[] args) {
        GameFacade.startGame(GAME_SEED);
    }
}
