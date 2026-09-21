import controller.GameFacade;

public class Main {

    private static final long GAME_SEED = 9L;

    public static void main(String[] args) {
        GameFacade.startGame(GAME_SEED);
    }
}
