package view.presenter.move;

import message.move.PieceEnteredBoard;
import utils.color.PlayerColorNames;
import view.presenter.EventPresenter;
import view.presenter.PieceCountLine;

/**
 Presents {@link PieceEnteredBoard}: it tells that a piece left Base, followed by the piece count of the player.
 */
public final class PieceEnteredBoardPresenter extends EventPresenter<PieceEnteredBoard> {

    /**
     Creates the presenter for {@link PieceEnteredBoard}.
     */
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
