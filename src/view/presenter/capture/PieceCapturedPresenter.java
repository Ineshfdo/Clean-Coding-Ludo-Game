package view.presenter.capture;

import message.capture.PieceCaptured;
import utils.color.PlayerColorNames;
import view.presenter.EventPresenter;
import view.presenter.PieceCountLine;

/**
 Presents {@link PieceCaptured}: it tells the square where the capture happened, and then the new piece count of the player who lost the piece.
 */
public final class PieceCapturedPresenter extends EventPresenter<PieceCaptured> {

    /**
     Creates the presenter for {@link PieceCaptured}.
     */
    public PieceCapturedPresenter() {
        super(PieceCaptured.class);
    }

    // Requirement 4: the landing square, then the captured player's new tally.
    @Override
    public String present(PieceCaptured message) {
        String captureLine = "  -> " + message.capturingPieceLabel() + " piece lands on square "
                + message.capturePosition() + ", captures " + message.capturedPieceLabel()
                + " and returns it to the base.";
        String countLine = "  -> " + PieceCountLine.describe(
                PlayerColorNames.displayNameOf(message.capturedPlayerColor()),
                message.piecesOnBoard(), message.piecesAtBase());

        return captureLine + "\n" + countLine;
    }
}
