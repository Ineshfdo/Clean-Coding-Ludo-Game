package model.player.strategy;

import config.enums.PlayerColor;

/**
 * Finds the strategy that plays for a colour.
 */
public interface PlayerStrategyLookup {

    /**
     * Looks up the strategy of a colour.
     *
     * @param color the colour of the player whose turn it is
     * @return the strategy of that colour
     */
    PlayerStrategy getStrategyFor(PlayerColor color);
}
