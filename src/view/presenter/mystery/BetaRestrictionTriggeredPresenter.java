package view.presenter.mystery;

import message.mystery.BetaRestrictionTriggered;
import view.presenter.EventPresenter;

// Wording for a Beta restriction sending a piece back to Base.
public final class BetaRestrictionTriggeredPresenter extends EventPresenter<BetaRestrictionTriggered> {

    public BetaRestrictionTriggeredPresenter() {
        super(BetaRestrictionTriggered.class);
    }

    @Override
    public String present(BetaRestrictionTriggered message) {
        return "  -> " + message.pieceLabel()
                + " rolled a 3 two rounds in a row while Beta-restricted and is sent back to Base!";
    }
}
