package view.presenter.move;

import message.move.PieceEnteredBoard;
import utils.color.PlayerColorNames;
import view.presenter.EventPresenter;
import view.presenter.PieceCountLine;

// Wording for a piece leaving Base, followed by the player's piece count.
public final class PieceEnteredBoardPresenter extends EventPresenter<PieceEnteredBoard> {

    public PieceEnteredBoardPresenter() {
        super(PieceEnteredBoard.class);
    }

    @Override
    public String present(PieceEnteredBoard message) {
        String colorName = PlayerColorNames.displayNameOf(message.playerColor());
        String movedLine = "  -> " + colorName + " player moves piece " + message.pieceLabel()
                + " to the starting point.";
        String countLine = "  -> " + PieceCountLine.describe(colorName, message.piecesOnBoard(), message.piecesAtBase());

        return movedLine + "\n" + countLine;
    }
}
