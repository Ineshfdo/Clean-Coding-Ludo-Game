package view.presenter;

import config.enums.PlayerColor;
import model.board.Board;

// Names a track cell "Approach(X)" when it is some color's Approach cell, else "Cell(X)".
public final class CellNames {

    private final Board board;

    public CellNames(Board board) {
        this.board = board;
    }

    public String labelOf(int position) {
        if (isApproachCell(position)) {
            return "Approach(" + position + ")";
        }

        return "Cell(" + position + ")";
    }

    private boolean isApproachCell(int position) {
        for (PlayerColor color : PlayerColor.values()) {
            if (board.getApproachCellPosition(color) == position) {
                return true;
            }
        }

        return false;
    }
}
