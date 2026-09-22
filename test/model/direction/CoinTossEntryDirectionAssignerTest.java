package model.direction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import config.enums.CoinTossResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.coin.CoinToss;

@DisplayName("CoinTossEntryDirectionAssigner")
class CoinTossEntryDirectionAssignerTest {

    private final CoinToss coinToss = mock(CoinToss.class);
    private final EntryDirectionAssigner assigner = new CoinTossEntryDirectionAssigner(coinToss);

    @Test
    void headsMeansClockwise() {
        when(coinToss.flip()).thenReturn(CoinTossResult.HEADS);

        EntryDirection entryDirection = assigner.assign();

        assertSame(ClockwiseMovementStrategy.getInstance(), entryDirection.getDirection());
    }

    @Test
    void tailsMeansCounterClockwise() {
        when(coinToss.flip()).thenReturn(CoinTossResult.TAILS);

        EntryDirection entryDirection = assigner.assign();

        assertSame(CounterClockwiseMovementStrategy.getInstance(), entryDirection.getDirection());
    }

    @Test
    void keepsTheTossResultThatDecidedTheDirection() {
        when(coinToss.flip()).thenReturn(CoinTossResult.TAILS);

        assertEquals(CoinTossResult.TAILS, assigner.assign().getTossResult());
    }

    @Test
    void tossesTheCoinExactlyOncePerAssignment() {
        when(coinToss.flip()).thenReturn(CoinTossResult.HEADS);

        assigner.assign();

        verify(coinToss).flip();
    }

    @Test
    void entryDirectionExposesWhatItWasBuiltWith() {
        MovementDirectionStrategy direction = ClockwiseMovementStrategy.getInstance();
        EntryDirection entryDirection = new EntryDirection(CoinTossResult.HEADS, direction);

        assertEquals(CoinTossResult.HEADS, entryDirection.getTossResult());
        assertSame(direction, entryDirection.getDirection());
    }
}
