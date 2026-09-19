package model.player.strategy;

import java.util.Map;

import config.enums.PlayerColor;

// T-16: looks up each player's own decision-making Strategy by color, falling back to a
// shared default for any color without a specific one - a future color-specific strategy
// only means adding one more map entry, not changing TurnEngine.
public final class PlayerStrategyRegistry {

    private final Map<PlayerColor, PlayerStrategy> strategiesByColor;
    private final PlayerStrategy defaultStrategy;

    public PlayerStrategyRegistry(
            Map<PlayerColor, PlayerStrategy> strategiesByColor, PlayerStrategy defaultStrategy) {
        this.strategiesByColor = strategiesByColor;
        this.defaultStrategy = defaultStrategy;
    }

    public PlayerStrategy getStrategyFor(PlayerColor color) {
        return strategiesByColor.getOrDefault(color, defaultStrategy);
    }
}
