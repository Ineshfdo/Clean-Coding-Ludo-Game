package controller;

import config.enums.PlayerColor;
import exception.PlayerNotFoundException;
import java.util.ArrayList;
import java.util.List;
import model.board.TurnOrderLayout;
import model.player.Player;

// Rule 3: turn order runs clockwise from a given color.
public final class TurnOrderBuilder {

    private final TurnOrderLayout turnOrderLayout;

    public TurnOrderBuilder(TurnOrderLayout turnOrderLayout) {
        this.turnOrderLayout = turnOrderLayout;
    }

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
