package model.board;

import config.enums.PlayerColor;

/**
 * The colours sit clockwise around the board, and this decides who plays after whom.
 */
public interface TurnOrderLayout {

    /**
     * Finds the colour that plays after the given colour.
     *
     * @param color the colour to start from
     * @return the next colour, clockwise
     */
    PlayerColor getNextColorClockwise(PlayerColor color);
}
