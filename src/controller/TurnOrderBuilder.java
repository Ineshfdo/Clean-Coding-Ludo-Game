package controller;

import config.enums.PlayerColor;
import exception.PlayerNotFoundException;
import java.util.ArrayList;
import java.util.List;
import model.board.TurnOrderLayout;
import model.player.Player;

/**
 * Builds the clockwise turn order (rule 3) that starts with a given colour.
 */
public final class TurnOrderBuilder {

    private final TurnOrderLayout turnOrderLayout;

    /**
     * Creates the builder.
     *
     * @param turnOrderLayout tells which colour sits next to which around the board
     */
    public TurnOrderBuilder(TurnOrderLayout turnOrderLayout) {
        this.turnOrderLayout = turnOrderLayout;
    }

    /**
     * Lists all players in play order.
     *
     * @param startingColor colour of the first player
     * @param allPlayers all players of the game
     * @return the players in clockwise order, starting with the given colour
     * @throws PlayerNotFoundException if no player has one of the colours in the order
     */
    public List<Player> buildFrom(PlayerColor startingColor, List<Player> allPlayers) {
        List<Player> turnOrder = new ArrayList<>();
        PlayerColor color = startingColor;

        for (int position = 0; position < allPlayers.size(); position++) {
            turnOrder.add(findPlayerByColor(allPlayers, color));
            color = turnOrderLayout.getNextColorClockwise(color);
        }

        return turnOrder;
    }

    private static Player findPlayerByColor(List<Player> allPlayers, PlayerColor color) {
        return allPlayers.stream()
            .filter(player -> player.getColor() == color)
            .findFirst()
            .orElseThrow(() -> new PlayerNotFoundException("No player with color " + color));
    }
}
