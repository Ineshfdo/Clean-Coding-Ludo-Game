package view.presenter;

import config.constant.BoardConstants;

/**
 * Writes the line that tells how many pieces a player has on the board and at Base.
 */
public final class PieceCountLine {

    private PieceCountLine() {
    }

    /**
     * Writes the line, for example: Red player now has 2/4 pieces on the board and 2/4 pieces on
     * the base.
     *
     * @param playerName the name of the player
     * @param piecesOnBoard the number of pieces on the board
     * @param piecesAtBase the number of pieces at Base
     * @return the console text
     */
    public static String describe(String playerName, int piecesOnBoard, int piecesAtBase) {
        return playerName + " player now has " + piecesOnBoard + "/" + BoardConstants.PIECES_PER_PLAYER
                + " pieces on the board and " + piecesAtBase + "/" + BoardConstants.PIECES_PER_PLAYER
                + " pieces on the base.";
    }
}
