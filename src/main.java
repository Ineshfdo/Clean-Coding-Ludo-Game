import controller.GameFacade;

public class main {

    private static final long GAME_SEED = 9L;

    public static void main(String[] args) {
        GameFacade.startGame(GAME_SEED);
    }
}
