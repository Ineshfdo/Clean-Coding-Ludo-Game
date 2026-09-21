package view.presenter.mystery;

import message.mystery.PieceTeleported;
import view.presenter.EventPresenter;

// Wording for a teleport from the Mystery Cell.
public final class PieceTeleportedPresenter extends EventPresenter<PieceTeleported> {

    public PieceTeleportedPresenter() {
        super(PieceTeleported.class);
    }

    // T-11: a Base destination has no cell to report.
    @Override
    public String present(PieceTeleported message) {
        String outcome = "  -> " + message.pieceLabel() + " landed on the Mystery Cell! Teleported to "
                + message.destinationLabel();

        if (message.newPosition() < 0) {
            return outcome + ".";
        }

        return outcome + " (cell " + message.newPosition() + ").";
    }
}
