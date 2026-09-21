package model.player.strategy;

import config.enums.PlayerColor;
import java.util.Map;

// Looks up each color's strategy, falling back to a shared default.
public final class PlayerStrategyRegistry implements PlayerStrategyLookup {

    private final Map<PlayerColor, PlayerStrategy> strategiesByColor;
    private final PlayerStrategy defaultStrategy;

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
