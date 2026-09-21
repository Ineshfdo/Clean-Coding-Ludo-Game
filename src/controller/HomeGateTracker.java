package controller;

import config.constant.TurnConstants;
import config.enums.PlayerColor;
import java.util.List;
import message.observer.GameMessagePublisher;
import message.turn.HomeGateOpened;
import model.player.Player;
import model.player.rule.home.HomeGateStatus;
import model.player.rule.roll.RollEvent;
import model.player.rule.roll.RollHook;

// T-7 home gate: counts each color's rolls taken while every opponent is already Home.
public final class HomeGateTracker implements HomeGateStatus, RollHook {

    private final int[] rollCountByColorOrdinal = new int[PlayerColor.values().length];

    // Counts this roll and announces it once, on the roll that opens the gate.
    @Override
    public void onRollAccepted(RollEvent roll, GameMessagePublisher messagePublisher) {
        Player player = roll.getPlayer();

        recordRoll(player, roll.getAllPlayers());

        if (wasOpenedByLatestRoll(player.getColor())) {
            messagePublisher.publish(new HomeGateOpened(player.getColor()));
        }
    }

    @Override
    public boolean isOpenFor(PlayerColor color) {
        return rollCountByColorOrdinal[color.ordinal()] >= TurnConstants.CONSECUTIVE_ROLLS_TO_OPEN_HOME_GATE;
    }

    private void recordRoll(Player player, List<Player> allPlayers) {
        int colorIndex = player.getColor().ordinal();
        boolean allOpponentsHome = areAllOpponentsHome(player, allPlayers);

        if (allOpponentsHome) {
            rollCountByColorOrdinal[colorIndex]++;
        } else {
            rollCountByColorOrdinal[colorIndex] = 0;
        }
    }

    // True only for the roll that opens the gate, not for the rolls after it.
    private boolean wasOpenedByLatestRoll(PlayerColor color) {
        return rollCountByColorOrdinal[color.ordinal()]
                == TurnConstants.CONSECUTIVE_ROLLS_TO_OPEN_HOME_GATE;
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
