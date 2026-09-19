package model.player.rule.home;

import config.enums.PlayerColor;

// Read-only view of the home gate: once open, a color no longer needs a capture to enter HomeStraight.
public interface HomeGateStatus {

    boolean isOpenFor(PlayerColor color);
}
