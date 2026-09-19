package controller;

import config.constant.TurnConstants;
import config.enums.PlayerColor;
import java.util.List;
import model.player.Player;
import model.player.rule.home.HomeGateStatus;

// T-7 home gate: counts each color's rolls taken while every opponent is already Home.
public final class HomeGateTracker implements HomeGateStatus {

    private final int[] rollsWithAllOpponentsHome = new int[PlayerColor.values().length];

    // Counts this roll for the player; true only on the roll that opens the gate.
    public boolean recordRoll(Player player, List<Player> allPlayers) {
        int colorIndex = player.getColor().ordinal();
        boolean allOpponentsHome = areAllOpponentsHome(player, allPlayers);

        if (allOpponentsHome) {
            rollsWithAllOpponentsHome[colorIndex]++;
        } else {
            rollsWithAllOpponentsHome[colorIndex] = 0;
        }

        return allOpponentsHome
                && rollsWithAllOpponentsHome[colorIndex] == TurnConstants.CONSECUTIVE_ROLLS_TO_OPEN_HOME_GATE;
    }

    @Override
    public boolean isOpenFor(PlayerColor color) {
        return rollsWithAllOpponentsHome[color.ordinal()] >= TurnConstants.CONSECUTIVE_ROLLS_TO_OPEN_HOME_GATE;
    }

    // Pieces still in Base do not count as Home.
    private static boolean areAllOpponentsHome(Player player, List<Player> allPlayers) {
        for (Player other : allPlayers) {
            if (other.getColor() != player.getColor() && !other.hasAllPiecesHome()) {
                return false;
            }
        }

        return true;
    }
}
