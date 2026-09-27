package view.presenter.game;

import config.enums.PlayerColor;
import java.util.List;
import message.game.GameOver;
import view.presenter.EventPresenter;

/**
 * Presents {@link GameOver}: it writes the banner with the final standings.
 */
public final class GameOverPresenter extends EventPresenter<GameOver> {

    private static final String GAME_OVER_BANNER_BORDER = "=".repeat(50);

    /**
     * Creates the presenter for {@link GameOver}.
     */
    public GameOverPresenter() {
        super(GameOver.class);
    }

    // GAME_OVER: ranks 1st..4th in finishing order.
    @Override
    public String present(GameOver message) {
        List<PlayerColor> finalStandings = message.finalStandings();
        String[] placeLabels = { "1st", "2nd", "3rd", "4th" };
        StringBuilder banner = new StringBuilder();
        banner.append('\n').append(GAME_OVER_BANNER_BORDER).append('\n');
        banner.append("                   GAME OVER!\n");
        banner.append(GAME_OVER_BANNER_BORDER).append('\n');
        banner.append("FINAL STANDINGS:\n");

        for (int placeIndex = 0; placeIndex < finalStandings.size(); placeIndex++) {
            banner.append(placeLabels[placeIndex]).append(" Place: ")
                    .append(finalStandings.get(placeIndex).name()).append('\n');
        }

        banner.append(GAME_OVER_BANNER_BORDER);

        return banner.toString();
    }
}
