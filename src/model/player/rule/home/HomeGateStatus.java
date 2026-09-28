package model.player.rule.home;

import config.enums.PlayerColor;

/**
 Read-only view of the home gate.
 Once the gate is open, a colour no longer needs a capture to enter its HomeStraight.
 */
public interface HomeGateStatus {

    /**
     Tells whether the gate is open.
     @param color the colour to check
     @return true when the gate is open for that colour
     */
    boolean isOpenFor(PlayerColor color);
}
