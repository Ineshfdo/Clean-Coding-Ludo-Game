package view.presenter;

import config.enums.PlayerColor;
import model.board.Board;

/**
 * Names a track cell Approach(X) when it is the Approach cell of a colour, and Cell(X) otherwise.
 */
public final class CellNames {

    private final Board board;

    /**
     * Creates the naming helper.
     *
     * @param board gives the Approach cells
     */
    public CellNames(Board board) {
        this.board = board;
    }

    /**
     * Names a track cell.
     *
     * @param position the track position
     * @return the name, for example Approach(26) or Cell(12)
     */
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
