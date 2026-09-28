package model.player.strategy;

import config.enums.PlayerColor;
import java.util.Map;

/**
 Registry of the strategy of each colour.
 A colour that has no strategy gets a shared default strategy.
 */
public final class PlayerStrategyRegistry implements PlayerStrategyLookup {

    private final Map<PlayerColor, PlayerStrategy> strategiesByColor;
    private final PlayerStrategy defaultStrategy;

    /**
     Creates the registry.
     @param strategiesByColor the strategy of each colour
     @param defaultStrategy the strategy for a colour that is not in the map
     */
    public PlayerStrategyRegistry(
            Map<PlayerColor, PlayerStrategy> strategiesByColor, PlayerStrategy defaultStrategy) {
        this.strategiesByColor = strategiesByColor;
        this.defaultStrategy = defaultStrategy;
    }

    @Override
    public PlayerStrategy getStrategyFor(PlayerColor color) {
        return strategiesByColor.getOrDefault(color, defaultStrategy);
    }
}
