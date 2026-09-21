package model.board;

import config.enums.PlayerColor;

// Colors sit clockwise around the board, which decides who plays after whom.
public interface TurnOrderLayout {

    PlayerColor getNextColorClockwise(PlayerColor color);
}
