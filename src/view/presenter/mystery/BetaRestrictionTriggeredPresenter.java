package view.presenter.mystery;

import message.mystery.BetaRestrictionTriggered;
import view.presenter.EventPresenter;

/**
 Presents {@link BetaRestrictionTriggered}: it tells that a Beta-restricted piece was sent back to Base after two rolls of 3 in a row.
 */
public final class BetaRestrictionTriggeredPresenter extends EventPresenter<BetaRestrictionTriggered> {

    /**
     Creates the presenter for {@link BetaRestrictionTriggered}.
     */
    public BetaRestrictionTriggeredPresenter() {
        super(BetaRestrictionTriggered.class);
    }

    @Override
    public String present(BetaRestrictionTriggered message) {
        return "  -> " + message.pieceLabel()
                + " rolled a 3 two rounds in a row while Beta-restricted and is sent back to Base!";
    }
}
