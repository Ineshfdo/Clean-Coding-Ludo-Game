package view.presenter.capture;

import message.capture.BlockCaptured;
import view.presenter.EventPresenter;

/**
 * Presents {@link BlockCaptured}: it tells that one blockade captured another and that all its
 * pieces return to Base.
 */
public final class BlockCapturedPresenter extends EventPresenter<BlockCaptured> {

    /**
     * Creates the presenter for {@link BlockCaptured}.
     */
    public BlockCapturedPresenter() {
        super(BlockCaptured.class);
    }

    @Override
    public String present(BlockCaptured message) {
        return "  -> Blockade " + message.capturingBlockLabel() + " captured Blockade "
                + message.capturedBlockLabel() + "! All of " + message.capturedBlockLabel()
                + " returns to Base.";
    }
}
