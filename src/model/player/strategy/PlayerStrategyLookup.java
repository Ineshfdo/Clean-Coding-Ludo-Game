package model.player.strategy;

import config.enums.PlayerColor;

// Finds the strategy that plays for a color.
public interface PlayerStrategyLookup {

    PlayerStrategy getStrategyFor(PlayerColor color);
}
