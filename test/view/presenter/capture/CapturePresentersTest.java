package view.presenter.capture;

import static org.junit.jupiter.api.Assertions.assertEquals;

import config.enums.PlayerColor;
import message.capture.BlockCaptured;
import message.capture.PieceCaptured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Capture presenters")
class CapturePresentersTest {

    @Test
    void pieceCapturedReportsTheCaptureAndTheVictimsPieceCounts() {
        String text = new PieceCapturedPresenter().present(new PieceCaptured("R1", 21, "G2", PlayerColor.GREEN, 2, 2));

        assertEquals("""
  -> R1 piece lands on square 21, captures G2 and returns it to the base.
  -> Green player now has 2/4 pieces on the board and 2/4 pieces on the base.\
""", text);
    }

    @Test
    void blockCapturedNamesBothBlockades() {
        String text = new BlockCapturedPresenter().present(new BlockCaptured("R1+R2", "G1+G2"));

        assertEquals("  -> Blockade R1+R2 captured Blockade G1+G2! All of G1+G2 returns to Base.", text);
    }
}
