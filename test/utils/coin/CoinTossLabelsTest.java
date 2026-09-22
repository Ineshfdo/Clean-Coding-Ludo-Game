package utils.coin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import config.enums.CoinTossResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CoinTossLabels")
class CoinTossLabelsTest {

    @Test
    void headsIsLabelledHeads() {
        assertEquals("Heads", CoinTossLabels.labelOf(CoinTossResult.HEADS));
    }

    @Test
    void tailsIsLabelledTails() {
        assertEquals("Tails", CoinTossLabels.labelOf(CoinTossResult.TAILS));
    }
}
