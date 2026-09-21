package view.presenter.capture;

import message.capture.BlockCaptured;
import view.presenter.EventPresenter;

// Wording for one blockade capturing another.
public final class BlockCapturedPresenter extends EventPresenter<BlockCaptured> {

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
