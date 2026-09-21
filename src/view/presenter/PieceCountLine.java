package view.presenter;

import config.constant.BoardConstants;

// "Red player now has 2/4 pieces on the board and 2/4 pieces on the base."
public final class PieceCountLine {

    private PieceCountLine() {
    }

    public static String describe(String playerName, int piecesOnBoard, int piecesAtBase) {
        return playerName + " player now has " + piecesOnBoard + "/" + BoardConstants.PIECES_PER_PLAYER
                + " pieces on the board and " + piecesAtBase + "/" + BoardConstants.PIECES_PER_PLAYER
                + " pieces on the base.";
    }
}
